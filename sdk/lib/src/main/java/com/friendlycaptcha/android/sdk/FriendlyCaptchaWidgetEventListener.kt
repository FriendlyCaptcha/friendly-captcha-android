/*!
 * Copyright (c) Friendly Captcha GmbH 2024.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
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