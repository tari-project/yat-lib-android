package yat.android.lib

import yat.android.api.LookupEmojiIdWithSymbolResponse
import yat.android.api.ResponseError
import yat.android.api.YatLibApiGateway

class YatLibApi() {
    private val yatLibGateway = YatLibApiGateway.create()

    suspend fun lookupEmojiIdWithSymbol(emojiId: String, symbol: String): LookupEmojiIdWithSymbolResponse {
        return try {
            yatLibGateway.lookupEmojiIdWithSymbol(emojiId, symbol)
        } catch (e: Throwable) {
            LookupEmojiIdWithSymbolResponse(false, listOf(), ResponseError(-1, e.toString()))
        }
    }
}