package yat.android.ui.onboarding.successDialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.widget.LinearLayout
import yat.android.R
import yat.android.databinding.YatLibDialogSuccessBinding

open class YatLibSuccessDialog(
    context: Context,
    emojiId: String,
    cancelable: Boolean = true,
    canceledOnTouchOutside: Boolean = true,
) {

    val dialog: Dialog = Dialog(context, R.style.YatLibBottomSlideDialog).apply {
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.yat_lib_dialog_success)
        setCancelable(cancelable)
        setCanceledOnTouchOutside(canceledOnTouchOutside)
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val ui = YatLibDialogSuccessBinding.bind(findViewById<LinearLayout>(R.id.dialog_root_view))
        ui.yat.text = emojiId
        ui.confirmButton.setOnClickListener { dismiss() }
        ui.closeButton.setOnClickListener { dismiss() }
        window?.setGravity(Gravity.BOTTOM)
    }

    fun show() = dialog.show()

    fun dismiss() = dialog.dismiss()
}
