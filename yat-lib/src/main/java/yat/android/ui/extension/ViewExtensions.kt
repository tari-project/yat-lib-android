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
package yat.android.ui.extension

import android.view.View
import android.widget.ProgressBar
import androidx.annotation.NonNull
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import java.lang.ref.WeakReference

internal fun View.visible() {
    this.visibility = View.VISIBLE
}

internal fun View.invisible() {
    this.visibility = View.INVISIBLE
}

internal fun View.gone() {
    this.visibility = View.GONE
}


internal fun ProgressBar.setColor(color: Int) {
    this.indeterminateDrawable
        .mutate()
        .colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        color, BlendModeCompat.SRC_IN
    )
}

internal fun View.temporarilyDisableClick() {
    isEnabled = false
    postDelayed(
        ClickEnablingRunnable(this),
        1000
    )
}

private class ClickEnablingRunnable(@NonNull view: View) : Runnable {

    private val viewWR: WeakReference<View> = WeakReference(view)

    override fun run() {
        viewWR.get()?.run {
            try {
                isEnabled = true
            } catch (e: Throwable) {
                // no-op
            }
        }
    }
}