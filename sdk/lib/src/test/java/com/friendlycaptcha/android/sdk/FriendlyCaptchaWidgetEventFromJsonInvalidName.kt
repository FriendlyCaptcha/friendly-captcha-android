package com.friendlycaptcha.android.sdk

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class FriendlyCaptchaWidgetEventInvalidNameTest {

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidWidgetCompleteEventFromJson() {
        val json = JSONObject("""
            {
                "name": "invalid.complete",
                "state": "completed",
                "response": "response_data",
                "id": "widget_id"
            }
        """)
        FriendlyCaptchaWidgetCompleteEvent.fromJson(json)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidWidgetErrorEventFromJson() {
        val json = JSONObject("""
            {
                "name": "invalid.error",
                "state": "error_state",
                "response": "response_data",
                "id": "widget_id",
                "error": {
                    "code": "error_code",
                    "detail": "error_detail"
                }
            }
        """)
        FriendlyCaptchaWidgetErrorEvent.fromJson(json)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidWidgetExpireEventFromJson() {
        val json = JSONObject("""
            {
                "name": "invalid.expire",
                "state": "expired",
                "response": "response_data",
                "id": "widget_id"
            }
        """)
        FriendlyCaptchaWidgetExpireEvent.fromJson(json)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidWidgetStateChangeEventFromJson() {
        val json = JSONObject("""
            {
                "name": "invalid.statechange",
                "state": "state_changed",
                "response": "response_data",
                "id": "widget_id",
                "error": {
                    "code": "error_code",
                    "detail": "error_detail"
                }
            }
        """)
        FriendlyCaptchaWidgetStateChangeEvent.fromJson(json)
    }
}