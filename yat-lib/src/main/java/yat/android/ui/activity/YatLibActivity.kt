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
package yat.android.ui.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.orhanobut.logger.Logger
import yat.android.R
import yat.android.YatLib
import yat.android.data.YatCart
import yat.android.databinding.ActivityYatLibBinding
import yat.android.ui.fragment.DisplayYatFragment
import yat.android.ui.fragment.IntroPage1Fragment
import yat.android.ui.fragment.IntroPage2Fragment
import yat.android.ui.fragment.IntroPage3Fragment

internal class YatLibActivity :
    AppCompatActivity(),
    IntroPage1Fragment.Delegate,
    IntroPage2Fragment.Delegate,
    IntroPage3Fragment.Delegate,
    DisplayYatFragment.Delegate {

    private lateinit var ui: ActivityYatLibBinding
    private val pageChangeListener = PageChangeListener()
    private lateinit var pageIndicatorActiveBg: Drawable
    private lateinit var pageIndicatorPassiveBg: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(
            when (YatLib.colorMode) {
                YatLib.ColorMode.DARK -> R.style.AppTheme_Dark
                YatLib.ColorMode.LIGHT -> R.style.AppTheme_Light
            },
            true
        )
        ui = ActivityYatLibBinding.inflate(layoutInflater)
        setContentView(ui.root)
        ui.viewPager.adapter = ViewPagerAdapter(this)
        ui.viewPager.isUserInputEnabled = false
        ui.viewPager.offscreenPageLimit = 2
        ui.viewPager.registerOnPageChangeCallback(pageChangeListener)

        pageIndicatorActiveBg = ResourcesCompat.getDrawable(
            resources,
            R.drawable.page_indicator_active_bg,
            theme
        )!!
        pageIndicatorPassiveBg = ResourcesCompat.getDrawable(
            resources,
            R.drawable.page_indicator_passive_bg,
            theme
        )!!
    }

    private inner class ViewPagerAdapter(activity: FragmentActivity) :
        FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment =
            when (position) {
                0 -> IntroPage1Fragment(this@YatLibActivity)
                1 -> IntroPage2Fragment(this@YatLibActivity)
                2 -> IntroPage3Fragment(this@YatLibActivity)
                else -> error("Unexpected position: $position")
            }

        override fun getItemCount(): Int = 3

    }

    inner class PageChangeListener : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            ui.page1Indicator.background = pageIndicatorPassiveBg
            ui.page2Indicator.background = pageIndicatorPassiveBg
            ui.page3Indicator.background = pageIndicatorPassiveBg
            when (position) {
                0 -> ui.page1Indicator.background = pageIndicatorActiveBg
                1 -> ui.page2Indicator.background = pageIndicatorActiveBg
                2 -> ui.page3Indicator.background = pageIndicatorActiveBg
            }
        }

    }

    override fun onNext(fragment: IntroPage1Fragment) {
        ui.viewPager.currentItem = 1
    }

    override fun onNext(fragment: IntroPage2Fragment) {
        ui.viewPager.currentItem = 2
    }

    override fun onRandomYatSuccessful(
        fragment: IntroPage3Fragment,
        cart: YatCart
    ) {
        Logger.i("Random yat successful: ${cart.getYat()}")
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.enter_from_bottom, 0,
                0, R.anim.exit_to_bottom
            )
            .add(
                R.id.display_yat_fragment_container,
                DisplayYatFragment(cart, this),
                DisplayYatFragment::class.java.simpleName
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onBack(fragment: DisplayYatFragment) {
        onBackPressed()
    }

    override fun onClose(fragment: DisplayYatFragment) {
        finish()
    }

    override fun onComplete(fragment: DisplayYatFragment, yat: String) {
        YatLib.delegateWeakReference.get()?.onYatIntegrationComplete(yat)
        finish()
    }

    override fun onUpgradeToCustomYat(fragment: DisplayYatFragment) {
        val url = YatLib.yatWebAppBaseURL +
                "/partner/${YatLib.config.pathKey}" +
                "/create?refresh_token=${YatLib.credentials.refreshToken}"
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(browserIntent)
    }

    override fun onViewTerms(fragment: DisplayYatFragment) {
        val url = YatLib.yatTermsURL
        val browserIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(url)
        )
        startActivity(browserIntent)
    }

}