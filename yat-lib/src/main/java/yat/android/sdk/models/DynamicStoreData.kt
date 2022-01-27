package yat.android.sdk.models

import com.google.gson.annotations.SerializedName

import java.io.Serializable

data class DynamicStoreData(
    @SerializedName( "gif")
    val gif: String?,
    @SerializedName( "image")
    val image: String?,
    @SerializedName( "video")
    val video: String?,
    @SerializedName( "webm")
    val webm: String?,
    @SerializedName( "v_video")
    val verticalVideo: String?,
    @SerializedName( "v_webm")
    val verticalWebm: String?,
) : Serializable