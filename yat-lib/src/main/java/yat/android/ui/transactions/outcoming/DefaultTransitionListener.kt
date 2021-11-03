package yat.android.ui.transactions.outcoming

import androidx.transition.Transition

internal open class DefaultTransitionListener: Transition.TransitionListener {
    override fun onTransitionStart(transition: Transition) = Unit

    override fun onTransitionEnd(transition: Transition) = Unit

    override fun onTransitionCancel(transition: Transition) = Unit

    override fun onTransitionPause(transition: Transition) = Unit

    override fun onTransitionResume(transition: Transition) = Unit
}