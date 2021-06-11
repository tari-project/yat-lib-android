package yat.android.ui.deeplink

import android.content.Context
import android.net.Uri

internal interface DeeplinkProcessor {
    fun processDeeplink(context: Context, deepLink: Uri)
}