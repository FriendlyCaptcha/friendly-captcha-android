/*!
 * Copyright (c) Friendly Captcha GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package com.friendlycaptcha.android.sdk

import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONObject
import java.net.URLEncoder


/**
 * FriendlyCaptchaJSBridge is a bridge between the WebView and the Android app.
 * It allows the WebView to send messages to the Android app, and vice versa.
 */
internal class FriendlyCaptchaJSBridge(
    private val listener: (JSONObject) -> Unit,
    private val webView: WebView
) {

    /**
     * This method is called by the WebView when it wants to send a message to the Android app.
     * The message is a JSON string, which is parsed into a JSONObject and passed to the listener.
     */
    @JavascriptInterface
    fun receiveMessage(jsonData: String) {
        FriendlyCaptchaLog.d("Received message from WebView: $jsonData")
        try {
            val data = JSONObject(jsonData)
            listener(data)
        } catch (ex: Exception) {
            // Print the exception and ignore the message.
            FriendlyCaptchaLog.e("Failed to parse JSON message from WebView: $jsonData", ex)
        }
    }

    /**
     * Send a string to the WebView. It does basic escaping, but it's not foolproof.
     */
    private fun sendString(message: String) {
        val encoded = URLEncoder.encode(
            message.replace("'", "\\'").replace(" ", "%20"),
            "UTF-8",
        )

        (webView.context as? android.app.Activity)?.runOnUiThread {
            // We use `loadUrl` instead of `evaluateJavascript` to support Android < 4.4.
            webView.loadUrl("javascript:window.receiveMessage(%27$encoded%27)")
        }

    }

    /**
     * Sends `{"type": "frc:widget.start"}` to the WebView.
     */
    internal fun start() {
        sendString("{\"type\": \"frc:widget.start\"}")
    }

    /**
     * Sends `{"type": "frc:widget.reset"}` to the WebView.
     */
    internal fun reset() {
        sendString("{\"type\": \"frc:widget.reset\"}")
    }
}