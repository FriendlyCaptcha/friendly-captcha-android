package com.friendlycaptcha.android.sdk

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class FriendlyCaptchaWidgetEventTest {

    @Test
    fun testWidgetCompleteEventFromJson() {
        val json = JSONObject("""
            {
                "name": "frc:widget.complete",
                "state": "completed",
                "response": "response_data",
                "id": "widget_id"
            }
        """)
        val event = FriendlyCaptchaWidgetCompleteEvent.fromJson(json)
        assertEquals("frc:widget.complete", event.name)
        assertEquals("completed", event.state)
        assertEquals("response_data", event.response)
        assertEquals("widget_id", event.id)
    }

    @Test
    fun testWidgetErrorEventFromJson() {
        val json = JSONObject("""
            {
                "name": "frc:widget.error",
                "state": "error_state",
                "response": "response_data",
                "id": "widget_id",
                "error": {
                    "code": "error_code",
                    "detail": "error_detail"
                }
            }
        """)
        val event = FriendlyCaptchaWidgetErrorEvent.fromJson(json)
        assertEquals("frc:widget.error", event.name)
        assertEquals("error_state", event.state)
        assertEquals("response_data", event.response)
        assertEquals("widget_id", event.id)
        assertEquals("error_code", event.error.code)
        assertEquals("error_detail", event.error.detail)
    }

    @Test
    fun testWidgetExpireEventFromJson() {
        val json = JSONObject("""
            {
                "name": "frc:widget.expire",
                "state": "expired",
                "response": "response_data",
                "id": "widget_id"
            }
        """)
        val event = FriendlyCaptchaWidgetExpireEvent.fromJson(json)
        assertEquals("frc:widget.expire", event.name)
        assertEquals("expired", event.state)
        assertEquals("response_data", event.response)
        assertEquals("widget_id", event.id)
    }

    @Test
    fun testWidgetStateChangeEventFromJson() {
        val json = JSONObject("""
            {
                "name": "frc:widget.statechange",
                "state": "state_changed",
                "response": "response_data",
                "id": "widget_id",
                "error": {
                    "code": "error_code",
                    "detail": "error_detail"
                }
            }
        """)
        val event = FriendlyCaptchaWidgetStateChangeEvent.fromJson(json)
        assertEquals("frc:widget.statechange", event.name)
        assertEquals("state_changed", event.state)
        assertEquals("response_data", event.response)
        assertEquals("widget_id", event.id)
        assertNotNull(event.error)
        assertEquals("error_code", event.error?.code)
        assertEquals("error_detail", event.error?.detail)
    }

    @Test
    fun testWidgetStateChangeEventFromJsonWithoutError() {
        val json = JSONObject("""
            {
                "name": "frc:widget.statechange",
                "state": "state_changed",
                "response": "response_data",
                "id": "widget_id"
            }
        """)
        val event = FriendlyCaptchaWidgetStateChangeEvent.fromJson(json)
        assertEquals("frc:widget.statechange", event.name)
        assertEquals("state_changed", event.state)
        assertEquals("response_data", event.response)
        assertEquals("widget_id", event.id)
        assertNull(event.error)
    }
}