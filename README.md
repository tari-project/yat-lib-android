# Yat Partner Integration Android Framework

This framework is designed to make it easy for Yat partners to integrate the Yat purchase or connect flows into their apps.

This repository contains an example app module named `yat-lib-example` that illustrates the steps below. The `YatAppConfig` instance in the `MainActivity` of the sample app needs to be edited for the app to function correctly.

## Requirements

- Android OS 8.0+ (API level 26+)

## Installation

https://jitpack.io/#yat-labs/yat-lib-android

1. Add the JitPack repository in your `settings.gradle` as the last item of repositories:

    ```gradle
   dependencyResolutionManagement {
      repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
      repositories {
         // your other repositories ...
         maven { url "https://jitpack.io" }
      }
   }
    ```

2. Add the dependency (check the latest version on [the JitPack page](https://jitpack.io/#tari-project/yat-lib-android)) in your app `build.gradle` file

    ```gradle
    dependencies {
        implementation 'com.github.tari-project:yat-lib-android:0.4.1'
    }
   ```

## Usage

1. This library uses deep links to return from the Yat web application back to the partner application. URL scheme of the deep link is agreed upon in between the Yat development team and the integration partner. Currently used scheme is `{partner_key}://y.at?{query_params}`. Add deep link support to your activity using an intent filter.

    ```xml
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />

        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />

        <!--
            this example illustrates support for a deep link in the format
            myapp://y.at?{query_params}
        -->
        <data
            android:host="y.at"
            android:scheme="myapp" />
    </intent-filter>
    ```

2. Implement the Yat library delegate in the class that will handle the Yat integration (e.g. `MainActivity`).

    ```kotlin
    class MainActivity : AppCompatActivity(), YatIntegration.Delegate {

        // ...

        override fun onYatIntegrationComplete(yat: String) {
            /*
            * Code to run when the integration has completed successfully.
            */
        }

        override fun onYatIntegrationFailed(failureType: YatIntegration.FailureType) {
            /*
            * Code to run when the integration has failed.
            */
        }

        // ...

    }
    ```

3. Initialize the Yat library.

    ```kotlin
    class MainActivity : AppCompatActivity(), YatIntegration.Delegate {

        // ...

        private fun initializeYatLib() {
            
            // You can define your own environment(e.g. SandBox), or use the default Production one ("https://y.at/")
            val yourEnvironment = YatIntegration.Environment(
                yatAPIBaseURL = "https://a.yourdomain.y.at",
                yatWebAppBaseURL = "https://yourdomain.y.at",
            )
   
            val yourConfig = YatConfiguration(
                appReturnLink = "app://y.at?action", // deep link to return to the app
                organizationName = "Yat Labs",
                organizationKey = "yat",
            )
   
            YatIntegration.setup(
                context = this,
                config = yourConfig,
                colorMode = YatIntegration.ColorMode.LIGHT, // Choose between LIGHT and DARK
                delegate = this,
                environment = yourEnvironment,
            )
        }

        // ...

    }
    ```

4. Add the code that handles deep links.

    ```kotlin
    class MainActivity : AppCompatActivity(), YatIntegration.Delegate {

        // ...

        override fun onNewIntent(intent: Intent) {
            super.onNewIntent(intent)
            intent.data?.let { deepLink ->
                YatIntegration.processDeepLink(deepLink)
            }
        }

        // ...

    }
    ```

5. Start the library.

    ```kotlin
    class MainActivity : AppCompatActivity(), YatIntegration.Delegate {
   
        // Your Yat records
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
            // ...
        )
        
        // ...

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            
            // init your view  ...
   
            initializeYatLib()
   
            ui.getYatButton.setOnClickListener {
                YatIntegration.showOnboarding(
                    context = this@MainActivity,
                    yatRecords = yatRecords,
                )
            }
        }

        // ...

    }
    ```

## Looking up a Yat (OUTDATED TBA)

Below is an example call to the `YatLib.lookupYat` function to query for the records linked to a Yat and print them.

```kotlin
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
    for (record in lookupResponse.yatRecords) {
        val shortAddress =
            record.data.substring(0, 4) +
                    "..." +
                    record.data.substring(record.data.length - 4, record.data.length)
        println("${record.type} : $shortAddress")
    }
    ui.yatRecordsTitleTextView.visibility = View.VISIBLE
    ui.yatRecordsTextView.text = records
}
```
