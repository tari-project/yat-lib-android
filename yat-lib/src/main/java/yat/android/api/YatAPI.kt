/*
 * Copyright 2021 Yat Labs
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of
 * its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package yat.android.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import yat.android.BuildConfig
import yat.android.YatLib
import yat.android.data.YatCart
import yat.android.data.request.*
import yat.android.data.request.UserRegistrationRequest
import yat.android.data.response.AuthenticationResponse
import yat.android.data.response.UserRegistrationResponse
import yat.android.data.response.YatLookupResponse

internal interface YatAPI {

    companion object {

        internal val instance by lazy {
            Retrofit.Builder()
                .client(
                    OkHttpClient.Builder()
                        .also { client ->
                            if (BuildConfig.DEBUG) HttpLoggingInterceptor()
                                .apply { level = HttpLoggingInterceptor.Level.BODY }
                                .also { client.addInterceptor(it) }
                        }.build()
                )
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(YatLib.yatAPIBaseURL)
                .build()
                .create(YatAPI::class.java)
        }
    }

    @POST("users")
    fun registerUser(
        @Body request: UserRegistrationRequest
    ): Call<UserRegistrationResponse>

    @POST("auth/token")
    fun authenticate(
        @Body request: AuthenticationRequest
    ): Call<AuthenticationResponse>

    @POST("auth/token/refresh")
    fun refreshToken(
        @Body request: AccessTokenRefreshRequest
    ): Call<AuthenticationResponse>

    @DELETE("cart")
    fun clearCart(
        @Header("Authorization") authorizationHeader: String
    ): Call<YatCart>

    @POST("codes/{app_code}/random_yat")
    fun addRandomYatToCart(
        @Header("Authorization") authorizationHeader: String,
        @Path("app_code") appCode: String,
        @Body request: AddRandomYatToCartRequest
    ): Call<YatCart>

    @POST("cart/checkout")
    fun checkoutCart(
        @Header("Authorization") authorizationHeader: String,
        @Body request: CartCheckoutRequest
    ): Call<YatCart>

    @PATCH("emoji_id/{emoji_id}")
    fun updateYat(
        @Header("Authorization") authorizationHeader: String,
        @Path("emoji_id") yat: String,
        @Body request: YatUpdateRequest
    ): Call<Void>

    @GET("emoji_id/{emoji_id}")
    fun lookupYat(
        @Path("emoji_id") yat: String
    ): Call<YatLookupResponse>

}