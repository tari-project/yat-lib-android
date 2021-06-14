package yat.android.ui.deeplink

import android.content.Context
import android.net.Uri
import yat.android.YatLib
import yat.android.api.YatAPI
import yat.android.api.callback.VoidCallbackHandler
import yat.android.data.request.YatUpdateRequest

sealed class DeeplinkAction(val emojiId: String) {
    // default
    class None(emojiId: String) : DeeplinkAction(emojiId)

    // after the user has created and bought a new Yat through the partner flow
    class Create(emojiId: String) : DeeplinkAction(emojiId) {
        override fun execute(context: Context) {
            val delegate = YatLib.delegateWeakReference.get()
            // when a person aborts creating flow, it back "undefined"
            if (emojiId.isEmpty() || emojiId == "undefined") {
                delegate?.onYatIntegrationFailed(YatLib.FailureType.INVALID_DEEP_LINK)
            } else {
                updateEmojiId {
                    delegate?.onYatIntegrationComplete(emojiId)
                }
            }
        }
    }

    // from the success page after the Yat has been successfully linked to the wallet
    class Manage(emojiId: String, val refreshToken: String) : DeeplinkAction(emojiId)

    // when the user clicks 'Connect this Yat with [wallet]' p.s it's also meant to have the refresh_token in there
    class Connect(emojiId: String) : DeeplinkAction(emojiId)

    open fun execute(context: Context) {
        updateEmojiId {
            YatLib.delegateWeakReference.get()?.onYatIntegrationComplete(emojiId)
        }
    }

    protected fun updateEmojiId(onSuccess: () -> Unit) {
        YatAPI.instance.updateYat(
            "Bearer " + YatLib.jwtStorage.getAccessToken(),
            emojiId,
            YatUpdateRequest(insert = YatLib.yatRecords)
        ).enqueue(
            VoidCallbackHandler(
                onSuccess = onSuccess,
                onError = { _, _ ->
                    YatLib.delegateWeakReference.get()
                        ?.onYatIntegrationFailed(YatLib.FailureType.YAT_UPDATE_FAILED)
                }
            )
        )
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