package yat.android.api

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import yat.android.api.json.EmojiStoreKey
import yat.android.api.json.LoadValueFromKeyValueStoreResponse
import yat.android.api.lookup.LookupEmojiIdWithSymbolResponse
import yat.android.lib.YatIntegration

internal interface YatLibApiGateway {

    @GET("emoji_id/{emojiId}/{symbol}")
    suspend fun lookupEmojiIdWithSymbol(@Path("emojiId") emojiId: String, @Path("symbol") symbol: String): LookupEmojiIdWithSymbolResponse

    @GET("emoji_id/{emojiId}/json/{key}")
    suspend fun loadValueFromKeyValueStore(@Path("emojiId") emojiId: String, @Path("key") key: EmojiStoreKey): Response<LoadValueFromKeyValueStoreResponse>

    companion object {
        fun create(): YatLibApiGateway {
            val retrofit = Retrofit.Builder()
                .baseUrl(YatIntegration.environment.yatAPIBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(YatLibApiGateway::class.java)
        }
    }
}