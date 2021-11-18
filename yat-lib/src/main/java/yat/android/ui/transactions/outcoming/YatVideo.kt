package yat.android.ui.transactions.outcoming

sealed class YatVideo(val remoteUrl: String) {

    var localFile: String = ""

    object None : YatVideo("")

    class Vertical(fileUrl: String) : YatVideo(fileUrl)

    class Square(fileUrl: String) : YatVideo(fileUrl)
}