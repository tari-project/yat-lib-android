package yat.android.ui.transactions.outcoming

sealed class TransactionState() {
    object Init : TransactionState()
    object Pending : TransactionState()
    object Complete : TransactionState()
}