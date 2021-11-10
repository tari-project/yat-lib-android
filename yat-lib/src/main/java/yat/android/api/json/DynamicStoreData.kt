package yat.android.api.json

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DynamicStoreData(
    @SerializedName("gif")
    val gif: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("video")
    val video: String?,
    @SerializedName("webm")
    val webm: String?
) : Serializable