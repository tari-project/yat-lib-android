package yat.android.data.storage

interface YatJWTStorage {

    fun getAccessToken(): String?

    fun getRefreshToken(): String?

    fun put(token: OAuthTokenPair)

    fun clear()
}