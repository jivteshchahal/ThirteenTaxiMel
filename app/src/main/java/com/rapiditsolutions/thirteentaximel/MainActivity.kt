package com.rapiditsolutions.thirteentaximel

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.net.MailTo
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import android.net.ConnectivityManager
import android.os.PersistableBundle


class MainActivity : AppCompatActivity() {
    lateinit var theWebPage: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webURL = getString(R.string.webUrl)
        theWebPage =findViewById(R.id.wvServices1)
        if (isNetworkConnected()) {
            theWebPage.settings.javaScriptEnabled = true
            theWebPage.settings.pluginState = WebSettings.PluginState.ON
            if (savedInstanceState == null)
            {
                theWebPage.loadUrl(webURL);
            }
            theWebPage.webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    Toast.makeText(this@MainActivity, description, Toast.LENGTH_SHORT).show()
                }

                @TargetApi(Build.VERSION_CODES.M)
                override fun onReceivedError(
                    view: WebView,
                    req: WebResourceRequest,
                    rerr: WebResourceError
                ) {
                    // Redirect to deprecated method, so you can use it in all SDK versions
                    onReceivedError(
                        view,
                        rerr.errorCode,
                        rerr.description.toString(),
                        req.url.toString()
                    )
                }

                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    if (url.startsWith(getString(R.string.tagTelphone))) {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(url))
                        startActivity(intent)
                        view.reload()
                        return true
                    } else if (url.startsWith(getString(R.string.tagMailto))) {
                        val mt: MailTo = MailTo.parse(url)
                        val i = newEmailIntent(
                            this@MainActivity,
                            getString(R.string.emailAddressOfRep),
                            getString(R.string.mailSubject),
                            "Hello Sir," +
                                    "I want to book a taxi on",
                            getString(R.string.mailCC)
                        )
                        startActivity(i)
                        view.reload()
                        return true
                    }
                    view.loadUrl(url)
                    return true
                }

                private fun newEmailIntent(
                    context: Context,
                    address: String,
                    subject: String,
                    body: String,
                    cc: String
                ): Intent? {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                    intent.putExtra(Intent.EXTRA_TEXT, body)
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                    intent.putExtra(Intent.EXTRA_CC, cc)
                    intent.type = getString(R.string.tagMailtoMsg)
                    return intent
                }
            }
            theWebPage.loadUrl(webURL)
        }else{
            Toast.makeText(applicationContext,getString(R.string.tagToastNoInternet),Toast.LENGTH_LONG).show()
        }
        // Obtain the FirebaseAnalytics instance.
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
//        val bundle = Bundle()
//        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, android.R.id)
//        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name)
//        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image")
//        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    private fun isNetworkConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        theWebPage.saveState(outState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        theWebPage.restoreState(savedInstanceState)
    }

}