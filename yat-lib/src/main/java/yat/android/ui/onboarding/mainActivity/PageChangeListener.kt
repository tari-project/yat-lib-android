package yat.android.ui.onboarding.mainActivity

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import yat.android.R
import yat.android.databinding.YatLibActivityYatLibBinding

internal class PageChangeListener(context: Context, val ui: YatLibActivityYatLibBinding) : ViewPager2.OnPageChangeCallback() {

    private lateinit var pageIndicatorActiveBg: Drawable
    private lateinit var pageIndicatorPassiveBg: Drawable

    init {
        pageIndicatorActiveBg = ResourcesCompat.getDrawable(context.resources, R.drawable.yat_lib_page_indicator_active_bg, context.theme)!!
        pageIndicatorPassiveBg = ResourcesCompat.getDrawable(context.resources, R.drawable.yat_lib_page_indicator_passive_bg, context.theme)!!
    }

    override fun onPageSelected(position: Int) {
        super.onPageSelected(position)
        ui.page1Indicator.background = pageIndicatorPassiveBg
        ui.page2Indicator.background = pageIndicatorPassiveBg
        ui.page3Indicator.background = pageIndicatorPassiveBg
        val indicator = when (position) {
            0 -> ui.page1Indicator
            1 -> ui.page2Indicator
            2 -> ui.page3Indicator
            else -> null
        }
        indicator?.background = pageIndicatorActiveBg
    }
}