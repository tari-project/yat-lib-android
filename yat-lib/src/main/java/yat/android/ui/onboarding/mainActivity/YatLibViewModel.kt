package yat.android.ui.onboarding.mainActivity

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import yat.android.lib.YatIntegration

internal class YatLibViewModel() : ViewModel() {

    val onNext: MutableLiveData<Unit> = MutableLiveData()
    val onClose: MutableLiveData<Unit> = MutableLiveData()

    fun onNext() = onNext.postValue(Unit)

    fun onClose() = onClose.postValue(Unit)

    fun manageYatUri() = generateUri("partner/${YatIntegration.config.organizationKey}")

    fun connectYatUri() = generateUri("partner/${YatIntegration.config.organizationKey}/link-email")

    private fun generateUri(path: String) : Uri {
        val baseUri = Uri.parse(YatIntegration.yatWebAppBaseURL)
        val records = YatIntegration.yatRecords.joinToString("|") { "${it.type.name}=${it.data}" }
        val encodedUri = Uri.Builder().scheme(baseUri.scheme)
            .authority(baseUri.authority)
            .appendPath(path)
            .appendQueryParameter("addresses", records)
            .build()
        return Uri.parse(Uri.decode(encodedUri.toString()))
    }
}