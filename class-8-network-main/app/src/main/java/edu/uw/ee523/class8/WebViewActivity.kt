package edu.uw.ee523.class8

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity: AppCompatActivity() {

    private var mWebView: WebView? = null

    private var mWebDestEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        mWebView = findViewById(R.id.webview) as WebView
        mWebDestEditText = findViewById(R.id.webview_edit_text) as EditText


        // Setting the WebViewClient to allow the WebView to handle
        // redirects within the WebView, as opposed to being launched in a browser.
        mWebView!!.webViewClient = object : WebViewClient() {
            // Deprecated in API 24, but the alternative is not compatible with Android <19
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
    }

    fun loadWebsite(view: View?) {
        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.loadUrl(mWebDestEditText!!.text.toString())
    }
}