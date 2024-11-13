package com.friendlycaptcha.android.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONObject
import java.net.URLEncoder

/**
 * A handle to a FriendlyCaptcha widget.
 * You generally don't create this yourself, instead you use [FriendlyCaptchaSDK.createWidget].
 */
class FriendlyCaptchaWidgetHandle(
    context: Context,
    sitekey: String,
    apiEndpoint: String = "global",
    theme: String = "auto",
    language: String = "",
) {

    /**
     * Response is the `frc-captcha-response` value from the widget, this is the value you should send
     * to your server to verify the captcha.
     */
    var response: String = ".UNINITIALIZED"

    /**
     * State is the current state of the widget.
     * See the [Lifecycle](https://developer.friendlycaptcha.com/docs/v2/sdk/lifecycle) documentation for more information.
     */
    var state: String = "uninitialized"

    /**
     * isReady is a flag that indicates if the widget is ready to receive commands.
     * It is *almost* instant, but one may be able to call `start()` before it is ready. We
     * buffer the start command until the widget is ready using the `startPending` flag.
     */
    private var isReady = false
    private var startPending = false


    private val url: String = "file:///android_asset/index.html?" +
            "sitekey=${URLEncoder.encode(sitekey, "UTF-8")}" +
            "&endpoint=${URLEncoder.encode(apiEndpoint, "UTF-8")}" +
            "&theme=${URLEncoder.encode(theme, "UTF-8")}" +
            "&language=${URLEncoder.encode(language, "UTF-8")}"

    private var onComplete: ((FriendlyCaptchaWidgetCompleteEvent) -> Unit)? = null
    private var onError: ((FriendlyCaptchaWidgetErrorEvent) -> Unit)? = null
    private var onExpire: ((FriendlyCaptchaWidgetExpireEvent) -> Unit)? = null
    private var onStateChange: ((FriendlyCaptchaWidgetStateChangeEvent) -> Unit)? = null

    fun setOnCompleteListener(listener: (FriendlyCaptchaWidgetCompleteEvent) -> Unit) {
        onComplete = listener
    }

    fun setOnErrorListener(listener: (FriendlyCaptchaWidgetErrorEvent) -> Unit) {
        onError = listener
    }

    fun setOnExpireListener(listener: (FriendlyCaptchaWidgetExpireEvent) -> Unit) {
        onExpire = listener
    }

    fun setOnStateChangeListener(listener: (FriendlyCaptchaWidgetStateChangeEvent) -> Unit) {
        onStateChange = listener
    }

    @SuppressLint("SetJavaScriptEnabled")
    val webView: WebView = WebView(context).apply {

        webViewClient = object : WebViewClient() {
            // Handle links in the WebView so they open in the browser, instead of within the small WebView.
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
        settings.domStorageEnabled = true
        webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                FriendlyCaptchaLog.webview(consoleMessage)
                return true
            }
        }

        println("URL: $url")

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
    private val bridge: FriendlyCaptchaJSBridge = FriendlyCaptchaJSBridge({ json ->
        handleMessage(json)
    }, webView).apply {
        webView.addJavascriptInterface(this, "Android")
    }

    /**
     * Trigger the widget to start a challenge. The widget will start a challenge solving in the background.
     *
     * The behavior of the widget depends on the mode of the Friendly Captcha application (sitekey):
     *
     * * In `interactive` mode, the user will need to click the widget to complete the process.
     * * In `noninteractive` mode, the widget will complete the process automatically.
     */
    fun start() {
        if (isReady) {
            bridge.start()
        } else {
            startPending = true
        }
    }

    /**
     * handleMessage is called when we receive a message from the WebView.
     */
    private fun handleMessage(json: JSONObject) {
        when (json.getString("type")) {
            "ready" -> {
                if (startPending) {
                    start()
                    startPending = false
                }
                isReady = true
            }
            FriendlyCaptchaWidgetEvent.WIDGET_COMPLETE -> {
                val event = FriendlyCaptchaWidgetCompleteEvent.fromJson(json.getJSONObject("detail"))
                onComplete?.invoke(event)
            }
            FriendlyCaptchaWidgetEvent.WIDGET_ERROR -> {
                val event = FriendlyCaptchaWidgetErrorEvent.fromJson(json.getJSONObject("detail"))
                onError?.invoke(event)
            }
            FriendlyCaptchaWidgetEvent.WIDGET_EXPIRE -> {
                val event = FriendlyCaptchaWidgetExpireEvent.fromJson(json.getJSONObject("detail"))
                onExpire?.invoke(event)
            }
            FriendlyCaptchaWidgetEvent.WIDGET_STATECHANGE -> {
                val event = FriendlyCaptchaWidgetStateChangeEvent.fromJson(json.getJSONObject("detail"))
                onStateChange?.invoke(event)
            }
            else -> {
                FriendlyCaptchaLog.e("Unknown event type received: ${json.getString("type")}")
            }
        }
    }

    /**
     * Reset the widget, removing any progress. This way it can be started again for another challenge.
     */
    fun reset() {
        if (isReady) {
            bridge.reset()
        }
    }

    /**
     * Destroy the widget, removing it from the view hierarchy.
     *
     * After calling this method, the widget handle is no longer usable.
     */
    fun destroy() {
        webView.destroy()

        response = ".DESTROYED"
        state = "destroyed"

        // Remove listeners
        onComplete = null
        onError = null
        onExpire = null
        onStateChange = null
    }
}