package yat.android.ui.deeplink

import android.content.Context
import android.net.Uri
import yat.android.YatLib
import yat.android.api.YatAPI
import yat.android.api.callback.VoidCallbackHandler
import yat.android.data.request.YatUpdateRequest

internal class DeeplinkProcessorImpl : DeeplinkProcessor {
    override fun processDeeplink(context: Context, deepLink: Uri) {
        val action = DeeplinkAction.parse(deepLink)
        val delegate = YatLib.delegateWeakReference.get()
        if (action is DeeplinkAction.None) {
            delegate?.onYatIntegrationFailed(YatLib.FailureType.INVALID_DEEP_LINK)
            return
        }
        if (YatLib.jwtStorage.getAccessToken().isNullOrEmpty()) {
            delegate?.onYatIntegrationFailed(YatLib.FailureType.YAT_LIB_NOT_INITIALIZED)
            return
        }

        action.execute(context)
    }
}