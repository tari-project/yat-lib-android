package yat.android.ui.deeplink

import android.content.Context
import android.net.Uri
import yat.android.lib.YatIntegration

internal sealed class DeeplinkAction(val emojiId: String) {
    // default
    class None(emojiId: String) : DeeplinkAction(emojiId)

    // after the user has created and bought a new Yat through the partner flow
    class Create(emojiId: String) : DeeplinkAction(emojiId) {
        override fun execute(context: Context) {
            val delegate = YatIntegration.delegateWeakReference.get()
            // when a person aborts creating flow, it back "undefined"
            if (emojiId.isEmpty() || emojiId == "undefined") {
                delegate?.onYatIntegrationFailed(YatIntegration.FailureType.INVALID_DEEP_LINK)
            } else {
                delegate?.onYatIntegrationComplete(emojiId)
            }
        }
    }

    // from the success page after the Yat has been successfully linked to the wallet
    class Manage(emojiId: String, val refreshToken: String) : DeeplinkAction(emojiId)

    // when the user clicks 'Connect this Yat with [wallet]' p.s it's also meant to have the refresh_token in there
    class Connect(emojiId: String) : DeeplinkAction(emojiId)

    open fun execute(context: Context) {
        YatIntegration.delegateWeakReference.get()?.onYatIntegrationComplete(emojiId)
    }


    companion object {
        const val ACTION_PROPERTY = "action"
        const val EMOJI_ID_PROPERTY = "eid"
        const val REFRESH_TOKEN_PROPERTY = "refresh_token"

        fun parse(deepLink: Uri): DeeplinkAction {
            val action = deepLink.getQueryParameter(ACTION_PROPERTY)
            val emojiId = deepLink.getQueryParameter(EMOJI_ID_PROPERTY).orEmpty()
            val refreshToken = deepLink.getQueryParameter(REFRESH_TOKEN_PROPERTY).orEmpty()

            return when (action) {
                "create" -> Create(emojiId)
                "manage" -> Manage(emojiId, refreshToken)
                "connect" -> Connect(emojiId)
                else -> None(emojiId)
            }
        }
    }
}