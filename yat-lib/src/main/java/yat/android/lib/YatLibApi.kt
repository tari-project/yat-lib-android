package yat.android.lib

import retrofit2.Response
import yat.android.api.ResponseError
import yat.android.api.YatLibApiGateway
import yat.android.api.json.EmojiStoreKey
import yat.android.api.json.LoadValueFromKeyValueStoreResponse
import yat.android.api.lookup.LookupEmojiIdWithSymbolResponse

class YatLibApi() {
    private val yatLibGateway = YatLibApiGateway.create()

    suspend fun lookupEmojiIdWithSymbol(emojiId: String, symbol: String): LookupEmojiIdWithSymbolResponse {
        return try {
            yatLibGateway.lookupEmojiIdWithSymbol(emojiId, symbol)
        } catch (e: Throwable) {
            LookupEmojiIdWithSymbolResponse(false, listOf(), ResponseError(-1, e.toString()))
        }
    }

    suspend fun loadValueFromKeyValueStore(emojiId: String, key: EmojiStoreKey): Response<LoadValueFromKeyValueStoreResponse> {
        return try {
            yatLibGateway.loadValueFromKeyValueStore(emojiId, key)
        } catch (e: Throwable) {
            throw e
        }
    }
}