package yat.android.api.lookup

import com.google.gson.annotations.SerializedName
import yat.android.api.ResponseError
import yat.android.data.YatRecord
import java.io.Serializable

data class LookupEmojiIdWithSymbolResponse(
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("result")
    val result: List<YatRecord>?,
    @SerializedName("error")
    val error: ResponseError?
) : Serializable