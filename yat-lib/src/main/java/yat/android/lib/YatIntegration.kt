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
package yat.android.lib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ContextThemeWrapper
import yat.android.R
import yat.android.data.YatRecord
import yat.android.sdk.infrastructure.ApiClient
import yat.android.ui.deeplink.DeeplinkAction
import yat.android.ui.deeplink.DeeplinkProcessor
import yat.android.ui.deeplink.DeeplinkProcessorImpl
import yat.android.ui.onboarding.mainActivity.YatLibActivity
import yat.android.ui.onboarding.mainActivity.YatLibViewModel
import yat.android.ui.onboarding.successDialog.YatLibSuccessDialog
import java.lang.ref.WeakReference

/**
 * Main library class.
 *
 * @author Yat Labs
 */
@Suppress("unused")
class YatIntegration {

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

    sealed class Environment(val yatAPIBaseURL: String, val yatWebAppBaseURL: String) {
        data object SandBox : Environment("https://a.yat.fyi/", "https://yat.fyi/")
        data object Production : Environment("https://a.y.at/", "https://y.at/")
    }

    companion object {
        internal var isInitialized: Boolean = false

        private var deeplinkProcessor: DeeplinkProcessor = DeeplinkProcessorImpl()

        internal var config: YatConfiguration? = null
        internal var userId: String? = null
        internal var userPassword: String? = null
        internal var environment: Environment? = null
        internal var delegateWeakReference: WeakReference<Delegate>? = null
        internal var yatRecords: List<YatRecord> = emptyList()
        private var colorMode: ColorMode? = null

        internal val appStyle: Int
            get() = when (colorMode) {
                ColorMode.DARK -> R.style.YatLibAppTheme_Dark
                ColorMode.LIGHT -> R.style.YatLibAppTheme_Light
                else -> {
                    Log.e(this::class.java.simpleName, "YatIntegration hasn't been initialized")
                    R.style.YatLibAppTheme_Light
                }
            }

        @JvmStatic
        fun setup(
            context: Context,
            config: YatConfiguration,
            colorMode: ColorMode,
            delegate: Delegate,
            environment: Environment = Environment.Production,
        ) {
            isInitialized = true

            Companion.config = config
            Companion.colorMode = colorMode
            Companion.environment = environment
            ApiClient.baseUrl = environment.yatAPIBaseURL
            ApiClient.tokenStorage = SharedPrefsTokenStorage(context.getSharedPreferences("yat_lib", Context.MODE_PRIVATE))
            delegateWeakReference = WeakReference(delegate)
        }

        @JvmStatic
        fun showOnboarding(context: Context, yatRecords: List<YatRecord>) {
            Companion.yatRecords = yatRecords
            if (!isInitialized) {
                delegateWeakReference?.get()?.onYatIntegrationFailed(
                    FailureType.YAT_LIB_NOT_INITIALIZED
                )
                Log.e(this::class.java.simpleName, "YatIntegration hasn't been initialized")
                return
            }
            val intent = Intent(context, YatLibActivity::class.java)
            context.startActivity(intent)
        }

        @JvmStatic
        fun openCreateYatFlow(context: Activity) = openLink(context, YatLibViewModel().manageYatUri())

        @JvmStatic
        fun connectExistingYat(context: Activity) = openLink(context, YatLibViewModel().connectYatUri())

        @JvmStatic
        fun processDeepLink(context: Activity, deepLink: Uri) {
            if (deeplinkProcessor.isValidDeepLink(context, deepLink)) {
                showSuccessDialog(context, deepLink)
            }
        }

        private fun showSuccessDialog(context: Activity, deepLink: Uri) {
            val themeWrapper = ContextThemeWrapper(context, appStyle)
            val action = DeeplinkAction.parse(deepLink)
            action.execute(context)
            YatLibSuccessDialog(themeWrapper, action.emojiId).show()
        }

        private fun openLink(context: Activity, uri: Uri) = context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}