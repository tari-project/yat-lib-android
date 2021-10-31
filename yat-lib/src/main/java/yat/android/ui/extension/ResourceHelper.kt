package yat.android.ui.extension

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.core.content.ContextCompat

class ResourceHelper {

    companion object {

        fun getDrawable(context: Context?, drawableResId: Int): Drawable? {
            return if (context != null && context.resources != null) {
                ContextCompat.getDrawable(context, drawableResId)
            } else null
        }

        fun dpToPx(context: Context?, dp: Float): Float =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context?.resources?.displayMetrics)
    }
}