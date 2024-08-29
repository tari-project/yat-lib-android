package yat.android.ui.transactions.outcoming

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YatLibOutcomingTransactionData(
    val amount: Double,
    val currency: String,
    val yat: String,
) : Parcelable