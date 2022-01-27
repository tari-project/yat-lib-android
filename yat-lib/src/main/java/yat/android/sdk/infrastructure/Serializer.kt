package yat.android.sdk.infrastructure

import com.google.gson.Gson

object Serializer {

    @JvmStatic
    val gson: Gson by lazy {
        Gson()
    }
}
