package yat.android.ui.transactions.outcoming

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import yat.android.api.json.EmojiStoreKey
import yat.android.lib.YatIntegration
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class YatVisualizerService() {
    private val cachedRepository = YatVideoCacheRepository()
    private val httpTimeout = 20L

    suspend fun getVisualizedVideoForYat(filesDirectory: File, yat: String): YatVideo {
        val response = YatIntegration.yatApi.loadValueFromKeyValueStore(yat, EmojiStoreKey.VisualizerFileLocations)
        val body = response.body()?.data

        val yatVideo = when {
            !body?.verticalVideo.isNullOrEmpty() -> YatVideo.Vertical(body?.verticalVideo.orEmpty())
            !body?.webm.isNullOrEmpty() -> YatVideo.Square(body?.webm.orEmpty())
            else -> null
        } ?: return YatVideo.None

        val cachedByUrlVideo = cachedRepository.getVideoByRemoteUrl(filesDirectory, yat, yatVideo)
        if (cachedByUrlVideo != YatVideo.None) return cachedByUrlVideo

        val downloadedVideo = downloadFileFromStore(yatVideo) ?: return YatVideo.None
        cachedRepository.copyFileToLocalStore(filesDirectory, yat, downloadedVideo, yatVideo)
        return yatVideo
    }


    private fun downloadFileFromStore(yatVideo: YatVideo): ResponseBody? {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(httpTimeout, TimeUnit.SECONDS)
                .readTimeout(httpTimeout, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder().url(yatVideo.remoteUrl).build()
            val response = client.newCall(request).execute()
            val body = response.body
            val responseCode = response.code
            return if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE && body != null) {
                body
            } else {
                null
            }
        } catch (e: Throwable) {
            return null
        }
    }
}