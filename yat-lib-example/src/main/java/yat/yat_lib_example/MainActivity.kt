package yat.yat_lib_example

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import yat.android.YatAppConfig
import yat.android.YatLib
import yat.android.data.YatRecord
import yat.android.data.YatRecordType
import yat.android.data.response.SupportedEmojiSetResponse
import yat.android.data.response.YatLookupResponse
import yat.yat_lib_example.databinding.ActivityMainBinding
import java.util.*

internal class MainActivity :
    AppCompatActivity(),
    YatLib.Delegate {

    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        YatLib.initialize(this, this)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)
        initializeYatLib()
        ui.getAYatButton.setOnClickListener {
            YatLib.start(this@MainActivity)
        }
    }

    private fun initializeYatLib() {
        val config = YatAppConfig(
            name = "Super Cool Wallet",
            sourceName = "SCW",
            pathKey = "scw",
            pubKey = "{64 character hex public key}",
            code = "66b6a5aa-11b4-12a9-1c1e-84765ef174ab",
            authToken = "AppToken84765783"
        )
        val yatRecords = listOf(
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
        YatLib.setup(
            config = config,
            userId = UUID.randomUUID().toString().substring(0, 15),
            userPassword = UUID.randomUUID().toString().substring(0, 15),
            colorMode = YatLib.ColorMode.DARK,
            yatRecords = yatRecords
        )
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { deepLink ->
            YatLib.processDeepLink(this, deepLink)
        }
    }

    override fun onYatIntegrationComplete(yat: String) {
        ui.yatTextView.text = yat
        ui.yatContentView.visibility = View.VISIBLE
        ui.yatRecordsTitleTextView.visibility = View.INVISIBLE
        ui.getAYatButton.visibility = View.GONE
        loadSupportedEmojiSet()
        lookupYat(yat)
    }

    override fun onYatIntegrationFailed(failureType: YatLib.FailureType) {
        val errorMessage = String.format(
            resources.getString(R.string.error_yat_integration),
            failureType.toString()
        )
        displayErrorDialog(errorMessage)
    }

    private fun loadSupportedEmojiSet() {
        YatLib.getSupportedEmojiSet(
            onSuccess = { processSupportedEmojiSet(it) },
            onError = { _, _ ->
                val errorMessage = resources.getString(R.string.error_yat_supported_emoji_set)
                displayErrorDialog(errorMessage)
            }
        )
    }

    private fun lookupYat(yat: String) {
        YatLib.lookupYat(
            yat,
            onSuccess = { processLookupResponse(it) },
            onError = { _, _ ->
                val errorMessage = resources.getString(R.string.error_yat_lookup)
                displayErrorDialog(errorMessage)
            }
        )
    }

    private fun processLookupResponse(lookupResponse: YatLookupResponse) {
        var records = ""
        for (record in lookupResponse.yatRecords) {
            val shortAddress =
                record.data.substring(0, 4) +
                        "..." +
                        record.data.substring(record.data.length - 4, record.data.length)
            records += "${record.type} : $shortAddress\n"
        }
        ui.yatRecordsTitleTextView.visibility = View.VISIBLE
        ui.yatRecordsTextView.text = records
    }

    private fun processSupportedEmojiSet(response: SupportedEmojiSetResponse) {
        println(response.joinToString(", "))
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