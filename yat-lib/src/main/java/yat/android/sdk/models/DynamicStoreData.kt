package yat.android.sdk.models

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import java.io.Serializable

data class DynamicStoreData(
    @field:Json(name = "gif")
    val gif: String?,
    @field:Json(name = "image")
    val image: String?,
    @field:Json(name = "video")
    val video: String?,
    @field:Json(name = "webm")
    val webm: String?,
    @field:Json(name = "v_video")
    val verticalVideo: String?,
    @field:Json(name = "v_webm")
    val verticalWebm: String?,
) : Serializable