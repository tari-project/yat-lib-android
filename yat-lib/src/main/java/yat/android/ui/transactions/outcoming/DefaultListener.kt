package yat.android.ui.transactions.outcoming

import android.animation.Animator

internal open class DefaultListener: Animator.AnimatorListener {
    override fun onAnimationCancel(p0: Animator?) = Unit

    override fun onAnimationEnd(p0: Animator?) = Unit

    override fun onAnimationRepeat(p0: Animator?) = Unit

    override fun onAnimationStart(p0: Animator?) = Unit
}