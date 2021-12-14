package yat.android.lib

import android.content.SharedPreferences
import yat.android.sdk.infrastructure.TokenStorage

class SharedPrefsTokenStorage(private val prefs: SharedPreferences): TokenStorage {

    override var accessToken: String?
        get() = prefs.getString("access_token", null)
        set(value) = prefs.edit().run {
            putString("access_token", value)
            apply()
        }

    override var refreshToken: String?
        get() = prefs.getString("refresh_token", null)
        set(value) = prefs.edit().run {
            putString("refresh_token", value)
            apply()
        }
}