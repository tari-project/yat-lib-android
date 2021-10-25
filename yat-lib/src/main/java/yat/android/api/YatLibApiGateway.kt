package yat.android.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import yat.android.lib.YatIntegration

internal interface YatLibApiGateway {

    @GET("emoji_id/{emojiId}/{symbol}")
    suspend fun lookupEmojiIdWithSymbol(@Path("emojiId") emojiId: String, @Path("symbol") symbol: String): LookupEmojiIdWithSymbolResponse

    companion object {
        fun create(): YatLibApiGateway {
            val retrofit = Retrofit.Builder()
                .baseUrl(YatIntegration.yatAPIBaseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(YatLibApiGateway::class.java)
        }
    }
}