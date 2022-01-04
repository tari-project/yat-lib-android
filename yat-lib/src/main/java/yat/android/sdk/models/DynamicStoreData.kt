package yat.android.sdk.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import java.io.Serializable

data class DynamicStoreData(
    @Json(name = "gif")
    val gif: String?,
    @Json(name = "image")
    val image: String?,
    @Json(name = "video")
    val video: String?,
    @Json(name = "webm")
    val webm: String?,
    @Json(name = "v_video")
    val verticalVideo: String?,
    @Json(name = "v_webm")
    val verticalWebm: String?,
) : Serializable