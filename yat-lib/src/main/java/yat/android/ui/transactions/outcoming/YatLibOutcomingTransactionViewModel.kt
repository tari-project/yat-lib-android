package yat.android.ui.transactions.outcoming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import yat.android.api.json.EmojiStoreKey
import yat.android.lib.YatIntegration
import java.io.File
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

class YatLibOutcomingTransactionViewModel() : ViewModel() {

    private val httpTimeout = 20L

    val videoUrl: MutableLiveData<String> = MutableLiveData()

    fun startVideoDownload(cacheDirectory: File, data: YatLibOutcomingTransactionData) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = YatIntegration.yatApi.loadValueFromKeyValueStore(data.yat, EmojiStoreKey.VisualizerFileLocations)
            val url = response.body()?.data?.webm.orEmpty()

            if (url.isEmpty()) {
                videoUrl.postValue("")
            } else {
                downloadFileFromStore(cacheDirectory, data, url)
            }
        }
    }

    private fun downloadFileFromStore(cacheDirectory: File, data: YatLibOutcomingTransactionData, url: String) {
        try {
            val client = OkHttpClient.Builder()
                .connectTimeout(httpTimeout, TimeUnit.SECONDS)
                .readTimeout(httpTimeout, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val body = response.body
            val responseCode = response.code
            if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE && body != null) {
                copyFileToLocalStore(cacheDirectory, data, body)
            } else {
                videoUrl.postValue("")
            }
        } catch (e: Throwable) {
            videoUrl.postValue("")
        }
    }

    private fun copyFileToLocalStore(cacheDirectory: File, data: YatLibOutcomingTransactionData, file: ResponseBody) {
        val localFile = File.createTempFile("yat_${data.yat}", null, cacheDirectory)
        file.byteStream().apply { localFile.outputStream().use { fileOut -> copyTo(fileOut) } }
        videoUrl.postValue(localFile.absolutePath)
    }
}