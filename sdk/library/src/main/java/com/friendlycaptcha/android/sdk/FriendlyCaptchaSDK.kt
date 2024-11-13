package com.friendlycaptcha.android.sdk

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

private fun getDeviceLanguage(): String {
    val dl = Locale.getDefault()
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        dl.toLanguageTag()
    } else {
        dl.language // ISO 639 fallback
    }
}

private fun getConfigurationTheme(context: Context): String {
    return when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
        Configuration.UI_MODE_NIGHT_YES -> "dark"
        Configuration.UI_MODE_NIGHT_NO -> "light"
        Configuration.UI_MODE_NIGHT_UNDEFINED -> "auto"
        else -> "auto"
    }
}

/**
 * The main entry point for the FriendlyCaptcha SDK.
 * You would typically create one instance of this class and use it to create multiple widgets.
 *
 * @param context The Android context to use for the SDK.
 * @param apiEndpoint Global setting for the API endpoint to use for the SDK. Valid values are `"global"` and `"eu"` or a URL. Defaults to `"global"`.
 */
class FriendlyCaptchaSDK(
    private val context: Context,
    private val apiEndpoint: String = "global"
) {

    init {
        validateApiEndpoint(apiEndpoint)
    }

    private fun validateApiEndpoint(apiEndpoint: String) {
        val validEndpoints = listOf("global", "eu")
        if (apiEndpoint !in validEndpoints && !apiEndpoint.startsWith("http")) {
            throw IllegalArgumentException("Invalid API endpoint: $apiEndpoint. Valid values are 'global' and 'eu' or a HTTP(S) URL.")
        }
    }

    /**
     * Create a new FriendlyCaptcha widget.
     *
     * @param sitekey The sitekey to use for the widget. This value always starts with `FC`.
     * @param theme The theme to use for the widget. This can be `"light"`, `"dark"` or `"auto" (which makes the browser decide)`. If not specified the Android UI mode is used.
     * @param language The language to use for the widget. Defaults to the device language. Accepts values like `en` or `en-US`.
     */
    @JvmOverloads
    fun createWidget(sitekey: String, theme: String? = null, language: String? = null): FriendlyCaptchaWidgetHandle {
        val languageWithDeviceLanguageFallback = language ?: getDeviceLanguage()
        val themeWithFallback = theme ?: getConfigurationTheme(context)

        val widgetHandle = FriendlyCaptchaWidgetHandle(
            context, sitekey, apiEndpoint=apiEndpoint, theme=themeWithFallback, language=languageWithDeviceLanguageFallback,
        )
        return widgetHandle
    }
}