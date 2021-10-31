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
package yat.android.ui.components

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import java.util.*

internal enum class CustomFont(private val fileName: String) {

    ALLIANCENO1_BOLD("fonts/AllianceNo1_Bold.ttf"),
    ALLIANCENO1_BLACK("fonts/AllianceNo1_Black.ttf"),
    ALLIANCENO1_LIGHT("fonts/AllianceNo1_Light.ttf"),
    ALLIANCENO1_MEDIUM("fonts/AllianceNo1_Medium.ttf"),
    ALLIANCENO1_REGULAR("fonts/AllianceNo1_Regular.ttf"),
    ALLIANCENO1_SEMI_BOLD("fonts/AllianceNo1_SemiBold.ttf");

    fun asTypeface(context: Context): Typeface {
        return Typeface.createFromAsset(context.assets, fileName)
    }

    companion object {
        private const val sScheme = "http://schemas.android.com/apk/res-auto"
        private const val sAttribute = "customFont"

        fun fromString(fontName: String): CustomFont? {
            return values().firstOrNull { it.name == fontName.toUpperCase(Locale.getDefault()) }
        }

        fun getFromAttributeSet(context: Context, attr: AttributeSet): Typeface {
            val fontName = attr.getAttributeValue(sScheme, sAttribute)
            val fontNameRes = attr.getAttributeResourceValue(sScheme, sAttribute, 0)
            requireNotNull(fontName) { "You must provide \"$sAttribute\"" }
            val customFont = fromString(fontName) ?: fromString(context.getString(fontNameRes)) ?: throw Throwable("Font wasn't found")
            return customFont.asTypeface(context)
        }
    }
}