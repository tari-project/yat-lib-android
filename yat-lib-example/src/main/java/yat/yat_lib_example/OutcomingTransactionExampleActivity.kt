package yat.yat_lib_example

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import yat.android.ui.transactions.outcoming.TransactionState
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionActivity

class OutcomingTransactionExampleActivity() : YatLibOutcomingTransactionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch(Dispatchers.IO) {
            delay(10000)
            //do some work here
            setTransactionState(TransactionState.Complete)
            //or
//            setTransactionState(TransactionState.Failed)
        }
    }
}