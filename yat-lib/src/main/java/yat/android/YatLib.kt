/*
 * Copyright 2021 Yat Labs
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of
 * its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package yat.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import yat.android.api.callback.CallbackHandler
import yat.android.api.YatAPI
import yat.android.api.callback.VoidCallbackHandler
import yat.android.data.YatRecord
import yat.android.data.request.AuthenticationRequest
import yat.android.data.request.YatUpdateRequest
import yat.android.data.response.AuthenticationResponse
import yat.android.data.response.YatLookupResponse
import yat.android.ui.activity.YatLibActivity
import java.lang.ref.WeakReference

/**
 * Main library class.
 *
 * @author Yat Labs
 */
@Suppress("unused")
class YatLib {

    enum class FailureType {
        INVALID_DEEP_LINK,
        YAT_LIB_NOT_INITIALIZED,
        YAT_UPDATE_FAILED
    }

    interface Delegate {

        fun onYatIntegrationComplete(yat: String)
        fun onYatIntegrationFailed(failureType: FailureType)

    }

    enum class ColorMode {
        LIGHT,
        DARK,
    }

    companion object {

        internal const val signingAPIBaseURL = "https://partner-aurora.emojid.me"
        internal const val userActivationAPIBaseURL = "https://partner.scratch.emojid.me"
        internal const val yatAPIBaseURL = "https://api-dev.yat.rocks/"
        internal const val yatWebAppBaseURL = "https://dev.yat.rocks"
        internal const val yatTermsURL = "https://pre-waitlist.y.at/terms"

        internal lateinit var config: YatAppConfig
        internal lateinit var userId: String
        internal lateinit var userPassword: String
        internal lateinit var colorMode: ColorMode
        internal lateinit var delegateWeakReference: WeakReference<Delegate>
        internal lateinit var yatRecords: List<YatRecord>

        internal lateinit var credentials: AuthenticationResponse

        @JvmStatic
        fun initialize(
            config: YatAppConfig,
            userId: String,
            userPassword: String,
            colorMode: ColorMode,
            delegate: Delegate,
            yatRecords: List<YatRecord>,
        ) {
            this.config = config
            this.userId = userId
            this.userPassword = userPassword
            this.colorMode = colorMode
            this.delegateWeakReference = WeakReference(delegate)
            this.yatRecords = yatRecords

            Logger.addLogAdapter(AndroidLogAdapter())
        }

        @JvmStatic
        fun start(context: Context) {
            if (!this::config.isInitialized) {
                delegateWeakReference.get()?.onYatIntegrationFailed(
                    FailureType.YAT_LIB_NOT_INITIALIZED
                )
                return
            }
            val intent = Intent(context, YatLibActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun processDeepLink(deepLink: Uri) {
            val action = deepLink.getQueryParameter("action")
            val yat = deepLink.getQueryParameter("eid")
            if (action == null || action != "manage" || yat == null) {
                delegateWeakReference.get()?.onYatIntegrationFailed(
                    FailureType.INVALID_DEEP_LINK
                )
                return
            }
            if (!this::config.isInitialized || !this::credentials.isInitialized) {
                delegateWeakReference.get()?.onYatIntegrationFailed(
                    FailureType.YAT_LIB_NOT_INITIALIZED
                )
                return
            }
            YatAPI.instance.updateYat(
                "Bearer " + credentials.accessToken,
                yat,
                YatUpdateRequest(insert = yatRecords)
            ).enqueue(
                VoidCallbackHandler(
                    onSuccess = {
                        delegateWeakReference.get()?.onYatIntegrationComplete(yat)
                    },
                    onError = { _, _ ->
                        delegateWeakReference.get()?.onYatIntegrationFailed(
                            FailureType.YAT_UPDATE_FAILED
                        )
                    }
                )
            )
        }

        @JvmStatic
        fun lookupYat(
            yat: String,
            onSuccess: (response: YatLookupResponse) -> Unit,
            onError: (responseCode: Int?, error: Throwable?) -> Unit
        ) {
            YatAPI.instance.lookupYat(
                yat = yat
            ).enqueue(
                CallbackHandler(onSuccess, onError)
            )
        }

    }
}