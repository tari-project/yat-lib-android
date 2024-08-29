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
import yat.android.sdk.infrastructure.ClientException
import yat.android.sdk.infrastructure.ServerException
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionActivity
import yat.android.ui.transactions.outcoming.YatLibOutcomingTransactionData
import yat.yat_lib_example.databinding.ActivityMainBinding

internal class MainActivity : AppCompatActivity(), YatIntegration.Delegate {

    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)
        initializeYatLib()
        initSearchView()
        ui.getAYatButton.setOnClickListener { testGetYat() }
    }

    private fun initializeYatLib() {
        val config = YatConfiguration(
            appReturnLink = BuildConfig.YAT_ORGANIZATION_RETURN_URL,
            organizationName = BuildConfig.YAT_ORGANIZATION_NAME,
            organizationKey = BuildConfig.YAT_ORGANIZATION_KEY,
        )

        YatIntegration.setup(
            context = this,
            config = config,
            colorMode = YatIntegration.ColorMode.LIGHT,
            delegate = this,
        )
    }

    private fun initSearchView() {
        ui.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrEmpty()) return false

                testSearchYat(query)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean = false
        })
        ui.testOutcomingButton.setOnClickListener { testOutcomingTx() }
        ui.testIncomingButton.setOnClickListener { testIncomingTx() }
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

    private fun testIncomingTx() {
        YatIntegration.processDeepLink(
            context = this,
            deepLink = Uri.parse("tari://y.at?action?eid=%F0%9F%98%82%F0%9F%98%87%F0%9F%99%83%F0%9F%98%8D%F0%9F%A4%96&refresh_token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJkMzlmYThhZS0xNTlkLTRkOGYtOTRhYi0zY2Q5OTRjODkyOGQiLCJpc3MiOiJ5YXQiLCJleHAiOjE2NDQxNDYxMjEsInNjb3BlcyI6WyJ0b2tlbjpyZWZyZXNoIl0sImlzc3VlZCI6MTY0MTQ2NzcyMSwiYWN0aXZlMmZhIjowfQ.6v-7IzmpIvMmyPjNZhwR9UDm_Dq-8OsioFA2xkQF4sI")
        )
    }

    private fun testOutcomingTx() {
        val data = YatLibOutcomingTransactionData(
            amount = 10.2,
            currency = "ETH",
            yat = "\uD83D\uDE02\uD83D\uDE07\uD83D\uDE43\uD83D\uDE0D\uD83E\uDD16",
        )

        YatLibOutcomingTransactionActivity.start(
            context = this,
            outcomingTransactionData = data,
            type = OutcomingTransactionExampleActivity::class.java,
        )
    }

    private fun testGetYat() {
        val yatRecords = listOf(
//        YatRecord(
//            type = YatRecordType.ADA_ADDRESS,
//            data = "DdzFFzCqrhsgwQmeWNBTsG8VjYunBLK9GNR93GSLTGj1FeMm8kFoby2cTHxEHBEraHQXmgTtFGz7fThjDRNNvwzcaw6fQdkYySBneRas",
//        ),
//        YatRecord(
//            type = YatRecordType.DOT_ADDRESS,
//            data = "GC8fuEZG4E5epGf5KGXtcDfvrc6HXE7GJ5YnbiqSpqdQYLg",
//        ),
//        YatRecord(
//            type = YatRecordType.BTC_ADDRESS,
//            data = "1NDyJtNTjmwk5xPNhjgAMu4HDHigtobu1s",
//        ),
//        YatRecord(
//            type = YatRecordType.ETH_ADDRESS,
//            data = "108dEFa0272dC118EF03a7993e4fC7A8AcF3a3d1",
//        ),
//        YatRecord(
//            type = YatRecordType.XTR_PUBLICKEY,
//            data = "d2e4db6dac593a9af36987a35676838ede4f69684ba433baeed68bce048e111b",
//        ),
//        YatRecord(
//            type = YatRecordType.XMR_STANDARD_ADDRESS,
//            data = "4AdUndXHHZ6cfufTMvppY6JwXNouMBzSkbLYfpAV5Usx3skxNgYeYTRj5UzqtReoS44qo9mtmXCqY45DJ852K5Jv2684Rge",
//        ),
            YatRecord(
                type = YatRecordType.XTM_ADDRESS,
                data = "f473irukvRHqfaLoQjCDtCRX4MmhGeLBkS1QMBuCkRDjHJuWY3WNgSg8vD1oZwbq4JhCQsQagxK5ufMYRNPgTqeDb38",
            ),
        )

        YatIntegration.showOnboarding(this@MainActivity, yatRecords)
    }

    private fun testSearchYat(query: String) {
        val tariTag = YatRecordType.XTM_ADDRESS.serializedName

        lifecycleScope.launch(Dispatchers.IO) {
            val statusText: String = try {
                val response = YatLibApi.emojiIDApi.lookupEmojiIDPayment(query, tariTag)

                if (response.status) {
                    response.result.takeUnless { it.isNullOrEmpty() }
                        ?.map { it.key + ": " + it.value.address }
                        ?.joinToString("\n\n")
                        ?.let { "Found addresses:\n$it" }
                        ?: "No results found"
                } else {
                    response.error?.reason ?: "Error with no reason"
                }
            } catch (clientException: ClientException) {
                clientException.message ?: "Client Exception with no message"
            } catch (serverException: ServerException) {
                serverException.message ?: "Server Exception with no message"
            } catch (e: Exception) {
                "Exception: $e"
            }

            launch(Dispatchers.Main) {
                ui.searchResult.text = statusText
            }
        }
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