package yat.android.ui.onboarding.mainActivity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import yat.android.ui.onboarding.IntroPage1Fragment
import yat.android.ui.onboarding.IntroPage2Fragment
import yat.android.ui.onboarding.IntroPage3Fragment

internal class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun createFragment(position: Int): Fragment =
        when (position) {
            0 -> IntroPage1Fragment()
            1 -> IntroPage2Fragment()
            2 -> IntroPage3Fragment()
            else -> error("Unexpected position: $position")
        }

    override fun getItemCount(): Int = 3
}