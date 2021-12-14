package yat.android.lib

import yat.android.sdk.apis.*
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

    companion object {
        val apiKeyApi: ApiKeysApi by lazy { ApiKeysApi.shared }
        val cartApi: CartApi by lazy { CartApi.shared }
        val discountsApi: DiscountsApi by lazy { DiscountsApi.shared }
        val emojiApi: EmojiApi by lazy { EmojiApi.shared }
        val emojiIDApi: EmojiIDApi by lazy { EmojiIDApi.shared }
        val keyManagementApi: KeyManagementApi by lazy { KeyManagementApi.shared }
        val lootBoxesApi: LootBoxesApi by lazy { LootBoxesApi.shared }
        val lootBoxTypeApi: LootBoxTypeApi by lazy { LootBoxTypeApi.shared }
        val organizationApi: OrganizationApi by lazy { OrganizationApi.shared }
        val proxyApi: ProxyApi by lazy { ProxyApi.shared }
        val transferApi: TransferApi by lazy { TransferApi.shared }
        val userAuthenticationApi: UserAuthenticationApi by lazy { UserAuthenticationApi.shared }
        val userFeatureApi: UserFeatureApi by lazy { UserFeatureApi.shared }
        val userInterestApi: UserInterestApi by lazy { UserInterestApi.shared }
        val usersApi: UsersApi by lazy { UsersApi.shared }
        val walletsApi: WalletsApi by lazy { WalletsApi.shared }
    }
}
