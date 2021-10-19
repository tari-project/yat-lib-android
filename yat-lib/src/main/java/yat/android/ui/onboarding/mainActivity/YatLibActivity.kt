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
package yat.android.ui.onboarding.mainActivity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import yat.android.R
import yat.android.databinding.ActivityYatLibBinding
import yat.android.lib.YatIntegration

internal class YatLibActivity : AppCompatActivity(){

    private lateinit var ui: ActivityYatLibBinding
    private val viewModel : YatLibViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(
            when (YatIntegration.colorMode) {
                YatIntegration.ColorMode.DARK -> R.style.AppTheme_Dark
                YatIntegration.ColorMode.LIGHT -> R.style.AppTheme_Light
            },
            true
        )
        ui = ActivityYatLibBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setupUI()
        observeUI()
    }

    private fun setupUI() = with(ui) {
        viewPager.adapter = ViewPagerAdapter(this@YatLibActivity)
        viewPager.offscreenPageLimit = 2
        viewPager.registerOnPageChangeCallback(PageChangeListener(this@YatLibActivity, ui))
    }

    private fun observeUI() = with(viewModel) {
        onClose.observe(this@YatLibActivity) { finish() }

        onNext.observe(this@YatLibActivity) { ui.viewPager.currentItem += 1 }
    }

    override fun onBackPressed() {
        if (ui.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            ui.viewPager.currentItem = ui.viewPager.currentItem - 1
        }
    }
}