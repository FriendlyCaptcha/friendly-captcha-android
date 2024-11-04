package com.friendlycaptcha.android.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URLEncoder

class FriendlyCaptchaWidgetHandle(
    context: Context,
    sitekey: String,
    apiEndpoint: String = "global",
    theme: String = "auto",
    language: String = "",
) {

    private val url: String = "file:///android_asset/index.html?" +
            "sitekey=${URLEncoder.encode(sitekey, "UTF-8")}" +
            "&endpoint=${URLEncoder.encode(apiEndpoint, "UTF-8")}" +
            "&theme=${URLEncoder.encode(theme, "UTF-8")}" +
            "&language=${URLEncoder.encode(language, "UTF-8")}"
//private val url: String = "https://example.com"

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {
        webViewClient = WebViewClient()
        settings.javaScriptEnabled = true
        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                val level = consoleMessage?.messageLevel() ?: android.webkit.ConsoleMessage.MessageLevel.LOG
                Log.i("WebViewConsole", "[$level]" + (consoleMessage?.message() ?: "No message") + " at " + consoleMessage?.sourceId() + ":" + consoleMessage?.lineNumber())
                return true
            }
        }

        loadUrl(this@FriendlyCaptchaWidgetHandle.url)
    }

    init {
        // Set layout parameters to match parent
        webView.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    @SuppressLint("AddJavascriptInterface")
    val bridge: FriendlyCaptchaJSInterface = FriendlyCaptchaJSInterface({ json ->
        handleMessage(json)
    }, webView).apply {
        webView.addJavascriptInterface(this, "Android")
    }


    fun handleMessage(json: JSONObject) {
        Log.i("FriendlyCaptcha", "Received message: $json")
        // Answer
        bridge.sendString(json.toString())
    }
}