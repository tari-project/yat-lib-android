package yat.android.ui.transactions.outcoming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.util.logging.Level
import java.util.logging.Logger

class YatLibOutcomingTransactionViewModel() : ViewModel() {

    private val visualizerService: YatVisualizerService = YatVisualizerService()

    val visualizedVideo: MutableLiveData<YatVideo> = MutableLiveData()

    val state: MutableLiveData<TransactionState> = MutableLiveData()

    init {
        state.postValue(TransactionState.Init)
    }

    fun startVideoDownload(cacheDirectory: File, data: YatLibOutcomingTransactionData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val video = visualizerService.getVisualizedVideoForYat(cacheDirectory, data.yat)
                visualizedVideo.postValue(video)
            } catch (e: Throwable) {
                Logger.getGlobal().log(Level.WARNING, e.toString())
            }
        }
    }
}

