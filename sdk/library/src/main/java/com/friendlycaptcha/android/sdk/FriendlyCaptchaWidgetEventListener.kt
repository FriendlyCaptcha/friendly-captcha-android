package com.friendlycaptcha.android.sdk

interface OnWidgetCompleteListener {
    fun onComplete(event: FriendlyCaptchaWidgetCompleteEvent)
}

interface OnWidgetErrorListener {
    fun onError(event: FriendlyCaptchaWidgetErrorEvent)
}

interface OnWidgetExpireListener {
    fun onExpire(event: FriendlyCaptchaWidgetExpireEvent)
}

interface OnWidgetStateChangeListener {
    fun onStateChange(event: FriendlyCaptchaWidgetStateChangeEvent)
}