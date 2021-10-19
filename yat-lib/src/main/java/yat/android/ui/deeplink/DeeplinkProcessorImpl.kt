package yat.android.ui.deeplink

import android.content.Context
import android.net.Uri
import yat.android.lib.YatIntegration

internal class DeeplinkProcessorImpl : DeeplinkProcessor {
    override fun processDeeplink(context: Context, deepLink: Uri) {
        val action = DeeplinkAction.parse(deepLink)
        val delegate = YatIntegration.delegateWeakReference.get()
        if (action is DeeplinkAction.None) {
            delegate?.onYatIntegrationFailed(YatIntegration.FailureType.INVALID_DEEP_LINK)
            return
        }

        action.execute(context)
    }

    override fun isValidDeepLink(context: Context, deepLink: Uri) : Boolean {
        val returnLink = Uri.parse(YatIntegration.config.appReturnLink)
        return returnLink.scheme == deepLink.scheme && returnLink.host == deepLink.host
    }
}