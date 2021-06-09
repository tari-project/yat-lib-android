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
package yat.android.ui.fragment

import androidx.lifecycle.ViewModel
import yat.android.YatLib
import yat.android.api.callback.CallbackHandler
import yat.android.api.SigningAPI
import yat.android.api.UserActivationAPI
import yat.android.api.YatAPI
import yat.android.api.callback.VoidCallbackHandler
import yat.android.data.YatCart
import yat.android.data.YatUser
import yat.android.data.request.AddRandomYatToCartRequest
import yat.android.data.request.AuthenticationRequest
import yat.android.data.request.SignMessageRequest
import yat.android.data.request.UserRegistrationRequest
import yat.android.data.response.AuthenticationResponse
import yat.android.data.response.SignMessageResponse
import yat.android.data.response.UserRegistrationResponse
import yat.android.data.storage.OAuthTokenPair

internal class IntroPage3FragmentViewModel : ViewModel() {

    fun getRandomYat(
        onSuccess: (response: YatCart) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        authenticateUser(
            onSuccess = { authenticationResponse ->
                YatLib.jwtStorage.put(
                    OAuthTokenPair(
                        authenticationResponse.accessToken,
                        authenticationResponse.refreshToken
                    )
                )
                clearCart(
                    onSuccess = {
                        signMessage(
                            onSuccess = { signMessageResponse ->
                                addRandomYatToCart(
                                    signMessageResponse,
                                    onSuccess,
                                    onError
                                )
                            },
                            onError
                        )
                    },
                    onError
                )
            },
            onError = { _, _ ->
                registerAndActivate(
                    onSuccess = {
                        getRandomYat(onSuccess, onError)
                    },
                    onError
                )
            }
        )
    }

    private fun registerAndActivate(
        onSuccess: () -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        registerUser(
            onSuccess = { registrationResponse ->
                activateUser(
                    registrationResponse.user,
                    onSuccess,
                    onError
                )
            },
            onError
        )
    }

    private fun registerUser(
        onSuccess: (response: UserRegistrationResponse) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        val request = UserRegistrationRequest(
            alternate_id = YatLib.userId,
            password = YatLib.userPassword,
            source = YatLib.config.sourceName
        )
        YatAPI.instance.registerUser(request).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    private fun activateUser(
        user: YatUser,
        onSuccess: () -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        UserActivationAPI.instance.activateUser(
            appAuthToken = YatLib.config.authToken,
            userId = user.id
        ).enqueue(
            VoidCallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    private fun authenticateUser(
        onSuccess: (response: AuthenticationResponse) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        YatAPI.instance.authenticate(
            AuthenticationRequest(
                alternateId = YatLib.userId,
                password = YatLib.userPassword
            )
        ).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    private fun clearCart(
        onSuccess: (response: YatCart) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        YatAPI.instance.clearCart(
            "Bearer " + YatLib.jwtStorage.getAccessToken()!!
        ).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    private fun signMessage(
        onSuccess: (response: SignMessageResponse) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        SigningAPI.instance.signMessage(
            SignMessageRequest(alternateId = YatLib.userId)
        ).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    private fun addRandomYatToCart(
        signMessageResponse: SignMessageResponse,
        onSuccess: (response: YatCart) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        YatAPI.instance.addRandomYatToCart(
            authorizationHeader = "Bearer " + YatLib.jwtStorage.getAccessToken()!!,
            appCode = YatLib.config.code,
            request = AddRandomYatToCartRequest(
                nonce = signMessageResponse.nonce,
                signature = signMessageResponse.signature,
                publicKey = YatLib.config.pubKey
            )
        ).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

}