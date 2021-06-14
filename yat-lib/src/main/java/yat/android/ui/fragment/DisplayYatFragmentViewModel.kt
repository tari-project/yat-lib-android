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
import yat.android.api.callback.VoidCallbackHandler
import yat.android.api.YatAPI
import yat.android.data.YatCart
import yat.android.data.YatRecord
import yat.android.data.request.CartCheckoutRequest
import yat.android.data.request.YatUpdateRequest

internal class DisplayYatFragmentViewModel : ViewModel() {

    fun clearCart(
        onSuccess: (response: YatCart) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        YatAPI.instance.clearCart(
            "Bearer " + YatLib.jwtStorage.getAccessToken()
        ).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    fun checkout(
        onSuccess: (response: YatCart) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        YatAPI.instance.checkoutCart(
            "Bearer " + YatLib.jwtStorage.getAccessToken(),
            CartCheckoutRequest.free()
        ).enqueue(
            CallbackHandler(
                onSuccess = onSuccess,
                onError = onError
            )
        )
    }

    fun updateYat(
        yat: String,
        yatRecords: List<YatRecord>,
        onSuccess: (yat: String) -> Unit,
        onError: (responseCode: Int?, error: Throwable?) -> Unit
    ) {
        Thread.sleep(1000)
        YatAPI.instance.updateYat(
            "Bearer " + YatLib.jwtStorage.getAccessToken(),
            yat,
            YatUpdateRequest(insert = yatRecords)
        ).enqueue(
            VoidCallbackHandler(
                onSuccess = {
                    onSuccess(yat)
                },
                onError = onError
            )
        )
    }

}