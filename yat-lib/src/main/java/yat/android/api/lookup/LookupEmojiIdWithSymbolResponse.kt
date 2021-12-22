package yat.android.api.lookup

import com.google.gson.annotations.SerializedName
import yat.android.api.ResponseError
import java.io.Serializable

data class LookupEmojiIdWithSymbolResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("error")
    val error: ResponseError?
) : Serializable


