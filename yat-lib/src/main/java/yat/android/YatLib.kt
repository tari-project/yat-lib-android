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
import com.google.gson.Gson
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import yat.android.api.callback.CallbackHandler
import yat.android.api.YatAPI
import yat.android.data.YatRecord
import yat.android.data.response.SupportedEmojiSetResponse
import yat.android.data.response.YatLookupResponse
import yat.android.data.storage.PreferencesJWTStorage
import yat.android.data.storage.YatJWTStorage
import yat.android.ui.activity.YatLibActivity
import yat.android.ui.deeplink.DeeplinkProcessor
import yat.android.ui.deeplink.DeeplinkProcessorImpl
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

        private var deeplinkProcessor: DeeplinkProcessor = DeeplinkProcessorImpl()

        internal lateinit var config: YatAppConfig
        internal lateinit var userId: String
        internal lateinit var userPassword: String
        internal lateinit var colorMode: ColorMode
        internal lateinit var delegateWeakReference: WeakReference<Delegate>
        internal lateinit var yatRecords: List<YatRecord>

        lateinit var jwtStorage: YatJWTStorage
            private set

        @JvmStatic
        fun initialize(context: Context, delegate: Delegate) {
            this.jwtStorage = PreferencesJWTStorage(context, Gson())
            this.delegateWeakReference = WeakReference(delegate)

            Logger.addLogAdapter(AndroidLogAdapter())
        }

        @JvmStatic
        fun setup(
            config: YatAppConfig,
            userId: String,
            userPassword: String,
            colorMode: ColorMode,
            yatRecords: List<YatRecord>,
        ) {
            this.config = config
            this.userId = userId
            this.userPassword = userPassword
            this.colorMode = colorMode
            this.yatRecords = yatRecords
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
        fun processDeepLink(context: Context, deepLink: Uri) {
            deeplinkProcessor.processDeeplink(context, deepLink)
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

        @JvmStatic
        fun getSupportedEmojiSet(
            onSuccess: (response: SupportedEmojiSetResponse) -> Unit,
            onError: (responseCode: Int?, error: Throwable?) -> Unit
        ) {
            YatAPI.instance.getSupportedEmojiSet().enqueue(
                CallbackHandler(onSuccess, onError)
            )
        }
    }
}