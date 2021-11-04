package yat.yat_lib_example

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import yat.android.data.YatRecord
import yat.android.data.YatRecordType
import yat.android.lib.YatConfiguration
import yat.android.lib.YatIntegration
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionActivity
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionData
import yat.yat_lib_example.databinding.ActivityMainBinding
import java.util.*

internal class MainActivity : AppCompatActivity(), YatIntegration.Delegate {

    private lateinit var ui: ActivityMainBinding
    private val yatRecords = listOf(
        YatRecord(
            YatRecordType.ADA_ADDRESS,
            "DdzFFzCqrhsgwQmeWNBTsG8VjYunBLK9GNR93GSLTGj1FeMm8kFoby2cTHxEHBEraHQXmgTtFGz7fThjDRNNvwzcaw6fQdkYySBneRas"
        ),
        YatRecord(
            YatRecordType.DOT_ADDRESS,
            "GC8fuEZG4E5epGf5KGXtcDfvrc6HXE7GJ5YnbiqSpqdQYLg"
        ),
        YatRecord(
            YatRecordType.BTC_ADDRESS,
            "1NDyJtNTjmwk5xPNhjgAMu4HDHigtobu1s"
        ),
        YatRecord(
            YatRecordType.ETH_ADDRESS,
            "108dEFa0272dC118EF03a7993e4fC7A8AcF3a3d1"
        ),
        YatRecord(
            YatRecordType.TARI_PUBKEY,
            "d2e4db6dac593a9af36987a35676838ede4f69684ba433baeed68bce048e111b"
        ),
        YatRecord(
            YatRecordType.XMR_STANDARD_ADDRESS,
            "4AdUndXHHZ6cfufTMvppY6JwXNouMBzSkbLYfpAV5Usx3skxNgYeYTRj5UzqtReoS44qo9mtmXCqY45DJ852K5Jv2684Rge"
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)
        initializeYatLib()
        initSearchView()
        ui.getAYatButton.setOnClickListener { YatIntegration.showOnboarding(this@MainActivity, yatRecords) }
    }

    private fun initializeYatLib() {
        val config = YatConfiguration(
            organizationName = "",
            organizationKey = "",
            appReturnLink = ""
        )

        YatIntegration.setup(
            config = config,
            colorMode = YatIntegration.ColorMode.LIGHT,
            this
        )
    }

    private fun initSearchView() {
        ui.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) return false

                lifecycleScope.launch(Dispatchers.IO) {
                    val answer = YatIntegration.yatApi.lookupEmojiIdWithSymbol(p0.orEmpty(), "XTR")
                    launch(Dispatchers.Main) {
                        if (answer.status) {
                            ui.searchResult.text = answer.result.toString()
                        } else {
                            ui.searchResult.text = answer.error.reason
                        }
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean = false
        })
        val data = YatLibOutcomingTransactionData(10.2, "ETH", "\uD83D\uDC09\uD83D\uDC09\uD83D\uDC09")
        ui.testOutcomingButton.setOnClickListener {
            YatLibOutcomingTransactionActivity.start(
                this,
                data,
                OutcomingTransactionExampleActivity::class.java
            )
        }
        ui.testIncomingButton.setOnClickListener {
            YatIntegration.showSuccessDialog(this, Uri.parse("uri"))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { deepLink -> YatIntegration.processDeepLink(this, deepLink) }
    }

    override fun onYatIntegrationComplete(yat: String) {
        ui.yatTextView.text = yat
        ui.yatContentView.visibility = View.VISIBLE
        ui.yatRecordsTitleTextView.visibility = View.INVISIBLE
        ui.getAYatButton.visibility = View.GONE
    }

    override fun onYatIntegrationFailed(failureType: YatIntegration.FailureType) {
        val errorMessage = String.format(
            resources.getString(R.string.error_yat_integration),
            failureType.toString()
        )
        displayErrorDialog(errorMessage)
    }

    private fun displayErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.error))
            .setMessage(message)
            .setPositiveButton(
                resources.getString(R.string.ok),
                null
            )
            .setCancelable(true)
            .show()
    }
}