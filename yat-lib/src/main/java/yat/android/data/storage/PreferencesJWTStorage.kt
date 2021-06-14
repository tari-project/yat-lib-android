package yat.android.data.storage

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import de.adorsys.android.securestoragelibrary.SecurePreferences

internal class PreferencesJWTStorage(private val context: Context, private val gson: Gson) : YatJWTStorage {
    override fun getAccessToken(): String? =
        getTokens(context)
            ?.let { gson.fromJson(it, SerializedTokens::class.java) }
            ?.accessToken

    override fun getRefreshToken(): String? =
        getTokens(context)
            ?.let { gson.fromJson(it, SerializedTokens::class.java) }
            ?.refreshToken

    override fun put(token: OAuthTokenPair) =
        setTokens(context, gson.toJson(SerializedTokens(token)))

    override fun clear() = removeTokens(context)

    // https://github.com/adorsys/secure-storage-android/issues/28#issuecomment-424394160
    private fun setTokens(context: Context, value: String) {
        value.chunked(SECURE_STORAGE_CHUNK_SIZE)
            .also { SecurePreferences.setValue(context, KEY_YAT_TOKEN_PAIR_CHUNKS, it.size) }
            .forEachIndexed { index, chunk ->
                SecurePreferences.setValue(context, "$KEY_YAT_TOKEN_PAIR$index", chunk)
            }
    }

    private fun getTokens(context: Context): String? {
        val numberOfChunks =
            SecurePreferences.getIntValue(context, KEY_YAT_TOKEN_PAIR_CHUNKS, 0)
        return if (numberOfChunks == 0) null
        else (0 until numberOfChunks).joinToString(separator = "") { index ->
            SecurePreferences.getStringValue(context, "$KEY_YAT_TOKEN_PAIR$index", null)!!
        }
    }

    private fun removeTokens(context: Context) {
        val numberOfChunks =
            SecurePreferences.getIntValue(context, KEY_YAT_TOKEN_PAIR_CHUNKS, 0)
        (0 until numberOfChunks).map {
            SecurePreferences.removeValue(context, "$KEY_YAT_TOKEN_PAIR$it")
        }
        SecurePreferences.removeValue(context, KEY_YAT_TOKEN_PAIR_CHUNKS)
    }

    data class SerializedTokens(
        @Expose @SerializedName("access_token") val accessToken: String,
        @Expose @SerializedName("refresh_token") val refreshToken: String,
    ) {
        constructor(tokens: OAuthTokenPair) : this(tokens.accessToken, tokens.refreshToken)
    }

    private companion object {
        private const val KEY_YAT_TOKEN_PAIR = "LLQHMXVGY761IRJP8OHMO95MHCXWY6MFLF7TTGIU"
        private const val KEY_YAT_TOKEN_PAIR_CHUNKS = "${KEY_YAT_TOKEN_PAIR}_O95MHCXWY6MFLF7TTGIU"
        private const val SECURE_STORAGE_CHUNK_SIZE = 240
    }

}