package com.friendlycaptcha.android.sdk


import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject
import java.net.URLEncoder


class FriendlyCaptchaJSInterface(
    private val listener: (JSONObject) -> Unit,
    private val webView: WebView
) {

    @JavascriptInterface
    fun receiveMessage(jsonData: String) {
        try {
            val data = JSONObject(jsonData)
            listener(data)
        } catch (ex: Exception) {
            // Print the exception and ignore the message.
            Log.e("FriendlyCaptcha", "Failed to parse JSON message from WebView: $jsonData", ex)
        }
    }

    /**
     * Send a string to the WebView. It does basic escaping, but it's not foolproof.
     */
    fun sendString(message: String) {
        val encoded = URLEncoder.encode(message.replace("'", "\'"), "UTF-8")

        (webView.context as? android.app.Activity)?.runOnUiThread {
            webView.loadUrl("javascript:window.receiveMessage(%27$encoded%27)")
        }
        // We use `loadUrl` instead of `evaluateJavascript` to support Android < 4.4.

    }

}