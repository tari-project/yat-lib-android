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
import android.view.ContextThemeWrapper
import yat.android.R
import yat.android.data.YatRecord
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

    companion object {

        internal const val yatAPIBaseURL = "https://a.y.at/"
        internal const val yatWebAppBaseURL = "https://y.at/"

        private var deeplinkProcessor: DeeplinkProcessor = DeeplinkProcessorImpl()

        internal lateinit var config: YatConfiguration
        internal lateinit var userId: String
        internal lateinit var userPassword: String
        internal lateinit var colorMode: ColorMode
        internal lateinit var delegateWeakReference: WeakReference<Delegate>
        internal lateinit var yatRecords: List<YatRecord>
        var yatApi: YatLibApi = YatLibApi()

        @JvmStatic
        fun setup(config: YatConfiguration, colorMode: ColorMode, delegate: Delegate) {
            Companion.config = config
            Companion.colorMode = colorMode
            delegateWeakReference = WeakReference(delegate)
        }

        @JvmStatic
        fun showOnboarding(context: Context, yatRecords: List<YatRecord>) {
            Companion.yatRecords = yatRecords
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
        fun openCreateYatFlow(context: Activity) = openLink(context, YatLibViewModel().manageYatUri())

        @JvmStatic
        fun connectExistingYat(context: Activity) = openLink(context, YatLibViewModel().connectYatUri())

        @JvmStatic
        fun processDeepLink(context: Activity, deepLink: Uri) {
            if (deeplinkProcessor.isValidDeepLink(context, deepLink)) {
                showSuccessDialog(context, deepLink)
            }
        }

        fun showSuccessDialog(context: Activity, deepLink: Uri) {
            val theme = when (colorMode) {
                ColorMode.DARK -> R.style.YatLibAppTheme_Dark
                ColorMode.LIGHT -> R.style.YatLibAppTheme_Light
            }
            val themeWrapper = ContextThemeWrapper(context, theme)
            YatLibSuccessDialog(themeWrapper, DeeplinkAction.parse(deepLink).emojiId).show()
        }

        private fun openLink(context: Activity, uri: Uri) = context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    }
}