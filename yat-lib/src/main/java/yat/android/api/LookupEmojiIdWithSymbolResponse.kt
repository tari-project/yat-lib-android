package yat.android.api

import yat.android.data.YatRecord
import java.io.Serializable

data class LookupEmojiIdWithSymbolResponse(val status: Boolean, val result: List<YatRecord>, val error: ResponseError) : Serializable

