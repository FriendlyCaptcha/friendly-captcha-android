package com.friendlycaptcha.android.sdk

import android.content.Context

class FriendlyCaptchaSDK(
    private val context: Context,
    private val apiEndpoint: String = "global"
) {

    @JvmOverloads
    fun createWidget(sitekey: String, theme: String = "auto", language: String = ""): FriendlyCaptchaWidgetHandle {
        val widgetHandle = FriendlyCaptchaWidgetHandle(
            context, sitekey, apiEndpoint=apiEndpoint, theme=theme, language=language,
        )
        return widgetHandle
    }
}