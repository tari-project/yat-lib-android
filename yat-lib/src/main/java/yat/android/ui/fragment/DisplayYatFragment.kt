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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import yat.android.R
import yat.android.YatLib
import yat.android.data.YatCart
import yat.android.databinding.FragmentDisplayYatBinding
import yat.android.ui.extension.*
import yat.android.ui.extension.displayErrorDialog
import yat.android.ui.extension.gone
import yat.android.ui.extension.invisible
import yat.android.ui.extension.visible
import java.lang.ref.WeakReference

internal class DisplayYatFragment(
    private val cart: YatCart,
    delegate: Delegate
) : Fragment() {

    interface Delegate {

        fun onBack(fragment: DisplayYatFragment)
        fun onClose(fragment: DisplayYatFragment)
        fun onComplete(fragment: DisplayYatFragment, yat: String)
        fun onUpgradeToCustomYat(fragment: DisplayYatFragment)
        fun onViewTerms(fragment: DisplayYatFragment)

    }

    private var _ui: FragmentDisplayYatBinding? = null
    private val ui get() = _ui!!
    private lateinit var viewModel: DisplayYatFragmentViewModel
    private val delegateWeakReference = WeakReference(delegate)
    private var checkedOutCart: YatCart? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel =
            ViewModelProvider(requireActivity()).get(
                DisplayYatFragmentViewModel::class.java
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _ui = FragmentDisplayYatBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ui.descriptionTextView.text = String.format(
            resources.getString(
                R.string.step_4_description,
                YatLib.config.name
            )
        )
        ui.checkoutProgressBar.setColor(
            resources.getColor(R.color.white, null)
        )
        ui.upgradeToCustomYatProgressBar.setColor(
            resources.getColor(R.color.white, null)
        )
        ui.yatTextView.text = cart.getYat()
        ui.gotItButton.setOnClickListener {
            it.temporarilyDisableClick()
            checkout()
        }
        ui.backButton.setOnClickListener {
            it.temporarilyDisableClick()
            delegateWeakReference.get()?.onBack(this)
        }
        ui.closeButton.setOnClickListener {
            it.temporarilyDisableClick()
            delegateWeakReference.get()?.onClose(this)
        }
        ui.upgradeToACustomYatButton.setOnClickListener {
            it.temporarilyDisableClick()
            onUpgradeToCustomYat()
        }
        ui.termsTextView.text = resources.getString(R.string.display_yat_terms).applyColorStyle(
            resources.getColor(R.color.x_light_gray, null),
            listOf(resources.getString(R.string.display_yat_terms_colored)),
            requireContext().getColorFromAttr(R.attr.primaryButtonColor)
        )
        ui.termsTextView.setOnClickListener {
            it.temporarilyDisableClick()
            delegateWeakReference.get()?.onViewTerms(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _ui = null
    }

    private fun checkout() {
        ui.gotItButton.invisible()
        ui.checkoutProgressBarContainer.visible()
        ui.upgradeToACustomYatButton.isEnabled = false
        checkedOutCart?.let {
            updateYat(it)
            return
        }
        viewModel.checkout(
            onSuccess = { cart ->
                checkedOutCart = cart
                updateYat(cart)
            },
            onError = { _, _ ->
                onCheckoutError()
            }
        )
    }

    private fun updateYat(cart: YatCart) {
        viewModel.updateYat(
            cart.getYat()!!,
            YatLib.yatRecords,
            onSuccess = { yat ->
                onComplete(yat)
            },
            onError = { _, _ ->
                onUpdateYatError()
            }
        )
    }

    private fun onCheckoutError() {
        ui.gotItButton.visible()
        ui.checkoutProgressBarContainer.gone()
        ui.upgradeToACustomYatButton.isEnabled = true
        displayErrorDialog(
            R.string.common_error_title,
            R.string.common_error_message_checkout
        )
    }

    private fun onUpdateYatError() {
        ui.gotItButton.visible()
        ui.checkoutProgressBarContainer.gone()
        ui.upgradeToACustomYatButton.isEnabled = false
        displayErrorDialog(
            R.string.common_error_title,
            R.string.common_error_message_update_yat
        )
    }

    private fun onComplete(yat: String) {
        ui.gotItButton.visible()
        ui.checkoutProgressBarContainer.gone()
        ui.upgradeToACustomYatButton.isEnabled = true
        delegateWeakReference.get()?.onComplete(
            this,
            yat
        )
    }

    private fun onUpgradeToCustomYat() {
        ui.gotItButton.isEnabled = false
        ui.upgradeToACustomYatButton.invisible()
        ui.upgradeToCustomYatProgressBarContainer.visible()
        viewModel.clearCart(
            onSuccess = {
                upgradeToCustomYat()
            },
            onError = { _, _ ->
                onUpgradeToCustomYatError()
            }
        )
    }

    private fun upgradeToCustomYat() {
        ui.gotItButton.isEnabled = true
        ui.upgradeToACustomYatButton.visible()
        ui.upgradeToCustomYatProgressBarContainer.gone()
        delegateWeakReference.get()?.onUpgradeToCustomYat(this)
    }

    private fun onUpgradeToCustomYatError() {
        ui.gotItButton.isEnabled = true
        ui.upgradeToACustomYatButton.visible()
        ui.upgradeToCustomYatProgressBarContainer.gone()
        displayErrorDialog(
            R.string.common_error_title,
            R.string.common_error_message_clear_cart
        )
    }

}