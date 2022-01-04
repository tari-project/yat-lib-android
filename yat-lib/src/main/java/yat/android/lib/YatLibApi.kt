package yat.android.lib

import yat.android.sdk.apis.*

class YatLibApi() {

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
