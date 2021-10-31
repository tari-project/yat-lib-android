package yat.android.ui.extension

import android.text.Html
import android.text.Spanned

object HtmlHelper {
    fun getSpannedText(text: String): Spanned = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
}


