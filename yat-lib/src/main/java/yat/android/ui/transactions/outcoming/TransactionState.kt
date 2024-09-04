package yat.android.ui.transactions.outcoming

sealed class TransactionState {
    data object Init : TransactionState()
    data object Pending : TransactionState()
    data object Complete : TransactionState()
    data object Failed: TransactionState()
}