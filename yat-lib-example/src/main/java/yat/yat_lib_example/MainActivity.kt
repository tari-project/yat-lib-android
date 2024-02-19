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
import yat.android.lib.YatLibApi
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionActivity
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionData
import yat.yat_lib_example.databinding.ActivityMainBinding

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
            YatRecordType.XTR_PUBLICKEY,
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
            appReturnLink = "tari://y.at?action"
        )

        YatIntegration.setup(
            context = this,
            config = config,
            colorMode = YatIntegration.ColorMode.LIGHT,
            delegate = this,
            environment = YatIntegration.Environment.SandBox,
        )
    }

    private fun initSearchView() {
        ui.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0.isNullOrEmpty()) return false

                lifecycleScope.launch(Dispatchers.IO) {
                    runCatching { YatLibApi.emojiIDApi.lookupEmojiIDPayment(p0, "0x0103") }.getOrNull()?.let { response->
                        launch(Dispatchers.Main) {
                            if (response.status) {
                                ui.searchResult.text = response.result?.map { it.key + " " + it.value.address }?.joinToString("")
                            } else {
                                ui.searchResult.text = response.error!!.reason
                            }
                        }
                    }
                }
                return false
            }

            override fun onQueryTextSubmit(p0: String?): Boolean = false
        })
        val data = YatLibOutcomingTransactionData(10.2, "ETH", "\uD83D\uDE02\uD83D\uDE07\uD83D\uDE43\uD83D\uDE0D\uD83E\uDD16")
        ui.testOutcomingButton.setOnClickListener {
            YatLibOutcomingTransactionActivity.start(
                this,
                data,
                OutcomingTransactionExampleActivity::class.java
            )
        }
        ui.testIncomingButton.setOnClickListener {
            YatIntegration.processDeepLink(this, Uri.parse("tari://y.at?action?eid=%F0%9F%98%82%F0%9F%98%87%F0%9F%99%83%F0%9F%98%8D%F0%9F%A4%96&refresh_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkMzlmYThhZS0xNTlkLTRkOGYtOTRhYi0zY2Q5OTRjODkyOGQiLCJpc3MiOiJ5YXQiLCJleHAiOjE2NDQxNDYxMjEsInNjb3BlcyI6WyJ0b2tlbjpyZWZyZXNoIl0sImlzc3VlZCI6MTY0MTQ2NzcyMSwiYWN0aXZlMmZhIjowfQ.6v-7IzmpIvMmyPjNZhwR9UDm_Dq-8OsioFA2xkQF4sI"))
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