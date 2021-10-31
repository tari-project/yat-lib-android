package yat.android.ui.transactions.outcoming

import java.io.Serializable

data class YatLibOutcomingTransactionData(val amount: Double, val currency: String, val yat: String) : Serializable