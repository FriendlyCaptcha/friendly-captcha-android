package com.friendlycaptcha.android.sdk

import org.json.JSONObject

open class FriendlyCaptchaWidgetEvent(
    val name: String,
    val state: String,
    val response: String,
    val id: String
) {
    companion object {
        const val WIDGET_COMPLETE = "frc:widget.complete"
        const val WIDGET_ERROR = "frc:widget.error"
        const val WIDGET_EXPIRE = "frc:widget.expire"
        const val WIDGET_STATECHANGE = "frc:widget.statechange"

        fun fromJson(json: JSONObject): FriendlyCaptchaWidgetEvent {
            val name = json.getString("name")
            val state = json.getString("state")
            val response = json.getString("response")
            val id = json.getString("id")
            return FriendlyCaptchaWidgetEvent(name, state, response, id)
        }
    }
}

class FriendlyCaptchaWidgetCompleteEvent(
    name: String,
    state: String,
    response: String,
    id: String
) : FriendlyCaptchaWidgetEvent(name, state, response, id) {
    companion object {
        fun fromJson(json: JSONObject): FriendlyCaptchaWidgetCompleteEvent {
            val name = json.getString("name")
            require(name == WIDGET_COMPLETE) { "Invalid event name: $name" }
            val state = json.getString("state")
            val response = json.getString("response")
            val id = json.getString("id")
            return FriendlyCaptchaWidgetCompleteEvent(name, state, response, id)
        }
    }
}

class FriendlyCaptchaWidgetErrorEvent(
    name: String,
    state: String,
    response: String,
    val error: WidgetError,
    id: String
) : FriendlyCaptchaWidgetEvent(name, state, response, id) {
    companion object {
        fun fromJson(json: JSONObject): FriendlyCaptchaWidgetErrorEvent {
            val name = json.getString("name")
            require(name == WIDGET_ERROR) { "Invalid event name: $name" }
            val state = json.getString("state")
            val response = json.getString("response")
            val id = json.getString("id")
            val errorJson = json.getJSONObject("error")
            val error = WidgetError(
                errorJson.getString("code"),
                errorJson.getString("detail")
            )
            return FriendlyCaptchaWidgetErrorEvent(name, state, response, error, id)
        }
    }
}

data class WidgetError(
    val code: String,
    val detail: String
)

class FriendlyCaptchaWidgetExpireEvent(
    name: String,
    state: String,
    response: String,
    id: String
) : FriendlyCaptchaWidgetEvent(name, state, response, id) {
    companion object {
        fun fromJson(json: JSONObject): FriendlyCaptchaWidgetExpireEvent {
            val name = json.getString("name")
            require(name == WIDGET_EXPIRE) { "Invalid event name: $name" }
            val state = json.getString("state")
            val response = json.getString("response")
            val id = json.getString("id")
            return FriendlyCaptchaWidgetExpireEvent(name, state, response, id)
        }
    }
}

class FriendlyCaptchaWidgetStateChangeEvent(
    name: String,
    state: String,
    response: String,
    val error: WidgetError? = null,
    id: String
) : FriendlyCaptchaWidgetEvent(name, state, response, id) {
    companion object {
        fun fromJson(json: JSONObject): FriendlyCaptchaWidgetStateChangeEvent {
            val name = json.getString("name")
            require(name == WIDGET_STATECHANGE) { "Invalid event name: $name" }
            val state = json.getString("state")
            val response = json.getString("response")
            val id = json.getString("id")
            val error = if (json.has("error")) {
                val errorJson = json.getJSONObject("error")
                WidgetError(
                    errorJson.getString("code"),
                    errorJson.getString("detail")
                )
            } else {
                null
            }
            return FriendlyCaptchaWidgetStateChangeEvent(name, state, response, error, id)
        }
    }
}