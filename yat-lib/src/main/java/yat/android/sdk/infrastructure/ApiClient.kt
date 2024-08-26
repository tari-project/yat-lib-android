package yat.android.sdk.infrastructure

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import yat.android.sdk.apis.UserAuthenticationApi
import yat.android.sdk.models.RefreshRequest
import yat.android.sdk.models.TokenResponse
import java.io.File
import java.net.URLConnection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.util.Date
import java.util.Locale

interface TokenStorage {
    var accessToken: String?
    var refreshToken: String?
}

private class InMemoryTokenStorage : TokenStorage {
    private var inMemoryAccessToken: String? = null
    private var inMemoryRefreshToken: String? = null

    override var accessToken: String?
        get() = inMemoryAccessToken
        set(value) {
            inMemoryAccessToken = value
        }
    override var refreshToken: String?
        get() = inMemoryRefreshToken
        set(value) {
            inMemoryRefreshToken = value
        }
}

open class ApiClient {
    companion object {
        protected const val CONTENT_TYPE = "Content-Type"
        protected const val ACCEPT = "Accept"
        protected const val AUTHORIZATION = "Authorization"
        protected const val BEARER = "Bearer"
        protected const val JSON_MEDIA_TYPE = "application/json"
        protected const val FORM_DATA_MEDIA_TYPE = "multipart/form-data"
        protected const val FORM_URL_ENC_MEDIA_TYPE = "application/x-www-form-urlencoded"
        protected const val XML_MEDIA_TYPE = "application/xml"

        var baseUrl: String = ""
        var tokenStorage: TokenStorage = InMemoryTokenStorage()
        var logLevel = HttpLoggingInterceptor.Level.NONE

        @JvmStatic
        val client: OkHttpClient by lazy {
            builder.authenticator(AccessTokenAuthenticator())
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = (logLevel)
            builder.addInterceptor(loggingInterceptor)
            builder.build()
        }

        @JvmStatic
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()

        fun logout() {
            tokenStorage.accessToken = null
            tokenStorage.refreshToken = null
        }
    }

    /**
     * Guess Content-Type header from the given file (defaults to "application/octet-stream").
     *
     * @param file The given file
     * @return The guessed Content-Type
     */
    protected fun guessContentTypeFromFile(file: File): String {
        val contentType = URLConnection.guessContentTypeFromName(file.name)
        return contentType ?: "application/octet-stream"
    }

    protected inline fun <reified T> requestBody(content: T, mediaType: String = JSON_MEDIA_TYPE): RequestBody =
        when {
            content is File -> content.asRequestBody(
                mediaType.toMediaTypeOrNull()
            )

            mediaType == FORM_DATA_MEDIA_TYPE -> {
                MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .apply {
                        // content's type *must* be Map<String, Any?>
                        @Suppress("UNCHECKED_CAST")
                        (content as Map<String, Any?>).forEach { (key, value) ->
                            if (value is File) {
                                val partHeaders = Headers.headersOf(
                                    "Content-Disposition",
                                    "form-data; name=\"$key\"; filename=\"${value.name}\""
                                )
                                val fileMediaType = guessContentTypeFromFile(value).toMediaTypeOrNull()
                                addPart(partHeaders, value.asRequestBody(fileMediaType))
                            } else {
                                val partHeaders = Headers.headersOf(
                                    "Content-Disposition",
                                    "form-data; name=\"$key\""
                                )
                                addPart(
                                    partHeaders,
                                    parameterToString(value).toRequestBody(null)
                                )
                            }
                        }
                    }.build()
            }

            mediaType == FORM_URL_ENC_MEDIA_TYPE -> {
                FormBody.Builder().apply {
                    // content's type *must* be Map<String, Any?>
                    @Suppress("UNCHECKED_CAST")
                    (content as Map<String, Any?>).forEach { (key, value) ->
                        add(key, parameterToString(value))
                    }
                }.build()
            }

            mediaType == JSON_MEDIA_TYPE -> Serializer.moshi.adapter(T::class.java).toJson(content).toRequestBody(
                mediaType.toMediaTypeOrNull()
            )

            mediaType == XML_MEDIA_TYPE -> throw UnsupportedOperationException("xml not currently supported.")
            // TODO: this should be extended with other serializers
            else -> throw UnsupportedOperationException("requestBody currently only supports JSON body and File body.")
        }

    protected inline fun <reified T : Any?> responseBody(body: ResponseBody?, mediaType: String? = JSON_MEDIA_TYPE): T? {
        if (body == null) {
            return null
        }
        val bodyContent = body.string()
        if (bodyContent.isEmpty()) {
            return null
        }
        when (mediaType) {
            JSON_MEDIA_TYPE -> {
                val bodyObject = Serializer.moshi.adapter(T::class.java).fromJson(bodyContent)
                if (bodyObject is TokenResponse) {
                    tokenStorage.accessToken = bodyObject.accessToken
                    tokenStorage.refreshToken = bodyObject.refreshToken
                }
                return bodyObject
            }

            else -> throw UnsupportedOperationException("responseBody currently only supports JSON body.")
        }
    }

    protected suspend inline fun <reified T : Any?> request(
        requestConfig: RequestConfig,
        body: Any? = null,
    ): ApiInfrastructureResponse<T?> = withContext(Dispatchers.IO) {
        val httpUrl = baseUrl.toHttpUrlOrNull() ?: throw IllegalStateException("baseUrl is invalid.")

        // add auth header
        tokenStorage.accessToken?.let { requestConfig.headers[AUTHORIZATION] = "Bearer $it" }

        val url = httpUrl.newBuilder()
            .addPathSegments(requestConfig.path.trimStart('/'))
            .apply {
                requestConfig.query.forEach { query ->
                    query.value.forEach { queryValue ->
                        addQueryParameter(query.key, queryValue)
                    }
                }
            }.build()

        // take content-type/accept from spec or set to default (application/json) if not defined
        if (requestConfig.headers[CONTENT_TYPE].isNullOrEmpty()) {
            requestConfig.headers[CONTENT_TYPE] = JSON_MEDIA_TYPE
        }
        if (requestConfig.headers[ACCEPT].isNullOrEmpty()) {
            requestConfig.headers[ACCEPT] = JSON_MEDIA_TYPE
        }
        val headers = requestConfig.headers

        if ((headers[CONTENT_TYPE] ?: "") == "") {
            throw kotlin.IllegalStateException("Missing Content-Type header. This is required.")
        }

        if ((headers[ACCEPT] ?: "") == "") {
            throw kotlin.IllegalStateException("Missing Accept header. This is required.")
        }

        // TODO: support multiple contentType options here.
        val contentType = (headers[CONTENT_TYPE] as String).substringBefore(";").lowercase(Locale.getDefault())

        val request = when (requestConfig.method) {
            RequestMethod.DELETE -> Request.Builder().url(url).delete(requestBody(body, contentType))
            RequestMethod.GET -> Request.Builder().url(url)
            RequestMethod.HEAD -> Request.Builder().url(url).head()
            RequestMethod.PATCH -> Request.Builder().url(url).patch(requestBody(body, contentType))
            RequestMethod.PUT -> Request.Builder().url(url).put(requestBody(body, contentType))
            RequestMethod.POST -> Request.Builder().url(url).post(requestBody(body, contentType))
            RequestMethod.OPTIONS -> Request.Builder().url(url).method("OPTIONS", null)
        }.apply {
            headers.forEach { header -> addHeader(header.key, header.value) }
        }.build()

        val response = client.newCall(request).execute()
        val accept = response.header(CONTENT_TYPE)?.substringBefore(";")?.lowercase(Locale.getDefault())

        // TODO: handle specific mapping types. e.g. Map<int, Class<?>>
        return@withContext when {
            response.isRedirect -> Redirection(
                statusCode = response.code,
                headers = response.headers.toMultimap(),
            )

            response.isInformational -> Informational(
                statusText = response.message,
                statusCode = response.code,
                headers = response.headers.toMultimap(),
            )

            response.isSuccessful -> Success(
                data = responseBody(response.body, accept),
                statusCode = response.code,
                headers = response.headers.toMultimap(),
            )

            response.isClientError -> ClientError(
                message = response.message,
                body = response.body?.string(),
                statusCode = response.code,
                headers = response.headers.toMultimap(),
            )

            else -> ServerError(
                message = response.message,
                body = response.body?.string(),
                statusCode = response.code,
                headers = response.headers.toMultimap(),
            )
        }
    }

    protected fun parameterToString(value: Any?): String = when (value) {
        null -> ""
        is Array<*> -> toMultiValue(value, "csv").toString()
        is Iterable<*> -> toMultiValue(value, "csv").toString()
        is OffsetDateTime, is OffsetTime, is LocalDateTime, is LocalDate, is LocalTime, is Date -> parseDateToQueryString(value)
        else -> value.toString()
    }

    inline fun <reified T : Any> parseDateToQueryString(value: T): String {
        /*
        .replace("\"", "") converts the json object string to an actual string for the query parameter.
        The moshi or gson adapter allows a more generic solution instead of trying to use a native
        formatter. It also easily allows to provide a simple way to define a custom date format pattern
        inside a gson/moshi adapter.
        */
        return Serializer.moshi.adapter(T::class.java).toJson(value).replace("\"", "")
    }

    private class AccessTokenAuthenticator : Authenticator {

        override fun authenticate(route: Route?, response: Response): Request? {
            val accessToken = tokenStorage.accessToken
            val refreshToken = tokenStorage.refreshToken
            val authHeader = response.request.header(AUTHORIZATION)
            if (accessToken == null || refreshToken == null) {
                return null
            } else if (authHeader == null || !authHeader.startsWith(BEARER)) {
                return null
            }

            synchronized(client) {
                // check if access token is refreshed in another thread
                if (accessToken != tokenStorage.accessToken) {
                    // token refreshed, continue with the new token
                    return response.request.newBuilder()
                        .header(AUTHORIZATION, "$BEARER ${tokenStorage.accessToken}")
                        .build()
                }
                // need to refresh the access token
                val tokenResponse = runBlocking { UserAuthenticationApi.shared.refreshToken(RefreshRequest(refreshToken)) }
                tokenStorage.accessToken = tokenResponse.accessToken
                tokenStorage.refreshToken = tokenResponse.refreshToken
                return response.request.newBuilder()
                    .header(AUTHORIZATION, "$BEARER ${tokenResponse.accessToken}")
                    .build()
            }
        }
    }
}
