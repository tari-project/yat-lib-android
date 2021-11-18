package yat.android.ui.transactions.outcoming

import android.animation.Animator
import android.app.ActionBar
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Surface
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.circularreveal.CircularRevealCompat
import com.google.android.material.circularreveal.CircularRevealWidget
import yat.android.R
import yat.android.databinding.YatLibOutcomingTransactionActivityBinding
import yat.android.ui.extension.HtmlHelper
import yat.android.ui.extension.ResourceHelper
import kotlin.math.sqrt


open class YatLibOutcomingTransactionActivity : AppCompatActivity() {

    private lateinit var ui: YatLibOutcomingTransactionActivityBinding
    private val viewModel: YatLibOutcomingTransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = YatLibOutcomingTransactionActivityBinding.inflate(layoutInflater)
        setContentView(ui.root)
        setupData(intent.getSerializableExtra(dataKey) as YatLibOutcomingTransactionData)
        window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        overridePendingTransition(0, 0)
        viewModel.state.observe(this) { processTransactionState(it) }
    }

    // public access
    fun setTransactionState(transactionState: TransactionState) = viewModel.state.postValue(transactionState)

    private fun setupData(data: YatLibOutcomingTransactionData) {
        viewModel.startVideoDownload(this.filesDir, data)
        val formattedAmount = data.amount.toString()
        val formattedWithCurrency = formattedAmount + " " + data.currency
        var spannedText = HtmlHelper.getSpannedText(getString(R.string.yat_lib_transaction_outcoming_text, formattedWithCurrency, data.yat))

        ui.yatLibMainInfo.post {
            val width = ui.yatLibMainInfo.paint.measureText(spannedText, 0, spannedText.length)
            val viewWidth = ui.yatLibMainInfo.width
            if (width > viewWidth + 350) {
                spannedText = HtmlHelper.getSpannedText(getString(R.string.yat_lib_transaction_outcoming_text_long_pattern, formattedWithCurrency, data.yat))
            }
            ui.yatLibMainInfo.text = spannedText
        }
    }

    private fun processTransactionState(state: TransactionState) {
        when (state) {
            TransactionState.Init -> showCircularRevealFromCenter(ui.yatLibRootReveal, true)
            TransactionState.Pending -> Unit
            TransactionState.Complete -> showSent()
            TransactionState.Failed -> showFailed()
        }
    }

    override fun onBackPressed() = showCircularRevealFromCenter(ui.yatLibRootReveal, false)

    private fun <T> showCircularRevealFromCenter(circularRevealWidget: T, isStraight: Boolean) where T : View?, T : CircularRevealWidget {
        circularRevealWidget.post {
            circularRevealWidget.visibility = View.VISIBLE
            val viewWidth: Int = circularRevealWidget.width
            val viewHeight: Int = circularRevealWidget.height
            val viewDiagonal = sqrt((viewWidth * viewWidth + viewHeight * viewHeight).toDouble()).toInt()
            val startRadius = if (isStraight) 0f else (viewDiagonal / 2).toFloat()
            val endRadius = if (isStraight) (viewDiagonal / 2).toFloat() else 0f
            CircularRevealCompat.createCircularReveal(
                circularRevealWidget,
                (viewWidth / 2).toFloat(),
                (viewHeight / 2).toFloat(),
                startRadius,
                endRadius
            )
                .apply {
                    duration = startUpAnimationDuration
                    addListener(object : DefaultListener() {
                        override fun onAnimationEnd(p0: Animator?) = onCircularRevealAnimationEnd(isStraight)
                    })
                    start()
                }
        }
    }

    private fun onCircularRevealAnimationEnd(isStraight: Boolean) = with(ui) {
        if (!isStraight) {
            yatLibRootReveal.visibility = View.GONE
            overridePendingTransition(0, 0)
            finish()
        } else {
            yatLibProgressBar.visibility = View.VISIBLE
            viewModel.visualizedVideo.observe(this@YatLibOutcomingTransactionActivity) {
                if (it !is YatVideo.None) {
                    showVideoByUrl(it.localFile)
                    showVideoAnimated(it)
                } else {
                    yatLibProgressBar.postDelayed({ showSent() }, artificialDelayBeforeSuccess)
                }
            }
        }
    }

    private fun showVideoByUrl(url: String) = with(ui) {
        yatLibVideo.animate().alpha(1F).setDuration(successAppearingAnimationDuration).start()
        try {
            MediaPlayer().apply {
                setDataSource(url)
                isLooping = true
                setSurface(Surface(yatLibVideoTextureView.surfaceTexture))
                setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                prepare()
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                start()
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    private fun showVideoAnimated(video: YatVideo) {
        ConstraintSet().apply {
            val bottomMargin = ResourceHelper.dpToPx(
                this@YatLibOutcomingTransactionActivity,
                resources.getDimension(R.dimen.yat_lib_outgoing_transaction_bottom_margin)
            )
            clone(ui.yatLibRootContainer)
            clear(R.id.yat_lib_main_info_container, ConstraintSet.TOP)
            clear(R.id.yat_lib_main_info_container, ConstraintSet.BOTTOM)
            connect(R.id.yat_lib_main_info_container, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, bottomMargin.toInt())
            val transition = AutoTransition().apply {
                duration = showVideoAnimationDuration
            }

            val isSquare = video !is YatVideo.Vertical
            val videoLayoutParams = ui.yatLibVideo.layoutParams as ConstraintLayout.LayoutParams

            if (isSquare) {
                videoLayoutParams.height = 0
                videoLayoutParams.bottomMargin = 0
                clear(R.id.yat_lib_video, ConstraintSet.BOTTOM)
                connect(R.id.yat_lib_video, ConstraintSet.BOTTOM, R.id.yat_lib_main_info_container, ConstraintSet.TOP)
                setDimensionRatio(R.id.yat_lib_video, "1:1")
            } else {
                videoLayoutParams.height = ActionBar.LayoutParams.WRAP_CONTENT
                videoLayoutParams.bottomMargin = ResourceHelper.dpToPx(this@YatLibOutcomingTransactionActivity, 50F).toInt()
                clear(R.id.yat_lib_video, ConstraintSet.BOTTOM)
                connect(R.id.yat_lib_video, ConstraintSet.BOTTOM, R.id.yat_lib_main_info_container, ConstraintSet.BOTTOM)
                setDimensionRatio(R.id.yat_lib_video, "")
            }
            ui.yatLibVideo.layoutParams = videoLayoutParams

            TransitionManager.beginDelayedTransition(ui.yatLibRootContainer, transition)
            applyTo(ui.yatLibRootContainer)
        }
    }


    private fun showFailed() {
        ui.yatLibSuccessfulTextView.setText(R.string.yat_lib_transaction_outcoming_failed)
        showSent()
    }

    private fun showSent() = with(ui) {
        yatLibSuccessfulText.animate().alpha(1F).setDuration(successAppearingAnimationDuration).start()
        yatLibVideo.animate().alpha(0F).setDuration(successAppearingAnimationDuration).start()
        yatLibMainInfoContainer.animate().alpha(0f).setDuration(successAppearingAnimationDuration).setListener(object : DefaultListener() {
            override fun onAnimationEnd(p0: Animator?) {
                yatLibRootContainer.postDelayed({
                    showCircularRevealFromCenter(ui.yatLibRootReveal, false)
                }, artificialDelayDuringSuccess)
            }
        }).start()
    }

    companion object {
        private const val dataKey = "YatLibDataKey"

        private const val startUpAnimationDuration = 800L
        private const val showVideoAnimationDuration = 1000L
        private const val artificialDelayBeforeSuccess = 6000L
        private const val successAppearingAnimationDuration = 1000L
        private const val artificialDelayDuringSuccess = 1000L

        fun <T : YatLibOutcomingTransactionActivity> start(
            context: Activity,
            outcomingTransactionData: YatLibOutcomingTransactionData,
            type: Class<T>
        ) {
            val intent = Intent(context, type)
            intent.putExtra(dataKey, outcomingTransactionData)
            intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context).toBundle())
        }
    }
}

