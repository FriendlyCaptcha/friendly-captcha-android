package com.friendlycaptcha.android.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
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

    var response: String = ""
    var state: String = "uninitialized"

    private val url: String = "file:///android_asset/index.html?" +
            "sitekey=${URLEncoder.encode(sitekey, "UTF-8")}" +
            "&endpoint=${URLEncoder.encode(apiEndpoint, "UTF-8")}" +
            "&theme=${URLEncoder.encode(theme, "UTF-8")}" +
            "&language=${URLEncoder.encode(language, "UTF-8")}"

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {

        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    request?.url.toString()
                } else {
                    request?.toString()
                }
                url?.let {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                    context.startActivity(intent)
                    return true
                }
                return false
            }
        }
        settings.javaScriptEnabled = true
        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                val level = consoleMessage?.messageLevel() ?: android.webkit.ConsoleMessage.MessageLevel.LOG
                val message = consoleMessage?.message() ?: "No message"
                val sourceId = consoleMessage?.sourceId() ?: "?"
                val lineNumber = consoleMessage?.lineNumber() ?: "?"

                if (level == android.webkit.ConsoleMessage.MessageLevel.ERROR) {
                    Log.e("FRC WebViewConsole", "[$level] $message at $sourceId:$lineNumber")
                } else if (level == android.webkit.ConsoleMessage.MessageLevel.WARNING) {
                    Log.w("FRC WebViewConsole", "[$level] $message at $sourceId:$lineNumber")
                } else {
                    Log.i("FRC WebViewConsole", "[$level] $message at $sourceId:$lineNumber")
                }
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
    val bridge: FriendlyCaptchaJSBridge = FriendlyCaptchaJSBridge({ json ->
        handleMessage(json)
    }, webView).apply {
        webView.addJavascriptInterface(this, "Android")
    }

    fun start() {
        bridge.start()
    }

    fun handleMessage(json: JSONObject) {
        val type = json.getString("type")

        when (type) {
            "ready" -> {
                // Widget is ready.. for now let's start it right away.
                start()
            }
            "frc:widget.statechange" -> {
                // Widget state has changed
                Log.i("FriendlyCaptcha", "Widget state change: ${json.getString("detail")}")
                val detail = json.getJSONObject("detail")
                response = detail.getString("response")
                state = detail.getString("state")

            }
        }
    }

    fun reset() {
        bridge.reset()
    }
}