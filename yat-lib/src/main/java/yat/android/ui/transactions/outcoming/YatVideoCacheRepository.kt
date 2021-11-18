package yat.android.ui.transactions.outcoming

import okhttp3.ResponseBody
import java.io.File

class YatVideoCacheRepository() {
    fun getVideoByRemoteUrl(filesDirectory: File, yat: String, yatVideo: YatVideo): YatVideo {
        val fileName = formatFileName(yat, yatVideo.remoteUrl)
        val files = getYatDirectory(filesDirectory).listFiles { _, s -> s == fileName }?.toList().orEmpty()
        if (files.isEmpty()) {
            return YatVideo.None
        }
        yatVideo.localFile = files.first().absolutePath
        return yatVideo
    }

    fun copyFileToLocalStore(filesDirectory: File, yat: String, body: ResponseBody, yatVideo: YatVideo): YatVideo {
        val fileName = formatFileName(yat, yatVideo.remoteUrl)
        val localFile = File(getYatDirectory(filesDirectory).absolutePath + "/" + fileName)
        body.byteStream().apply { localFile.outputStream().use { fileOut -> copyTo(fileOut) } }
        yatVideo.localFile = localFile.absolutePath
        return yatVideo
    }

    private fun getYatDirectory(filesDirectory: File) : File {
        val yatDir = File(filesDirectory, "yat")
        yatDir.mkdir()
        return yatDir
    }

    private fun formatFileName(yat: String, remoteUrl: String): String = "yat_" + yat + "_" +
            remoteUrl.split("-").lastOrNull { it.isNotEmpty() }.orEmpty().split(".").firstOrNull { it.isNotEmpty() } + ".webm"
}