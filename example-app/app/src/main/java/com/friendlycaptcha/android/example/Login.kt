package com.friendlycaptcha.android.example

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val statusCode: Int
)

const val ALWAYS_SUCCESS = false

suspend fun doLoginRequest(username: String, password: String, captchaResponse: String): LoginResponse {
    val url = URL("https://example.com/login")
    val json = JSONObject().apply {
        put("username", username)
        put("password", password)
        put("captchaResponse", captchaResponse)
    }
    val postData = json.toString().toByteArray()

    return withContext(Dispatchers.IO) {
        try {
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
            connection.doOutput = true
            connection.outputStream.use { it.write(postData) }

            val statusCode = connection.responseCode

            if (ALWAYS_SUCCESS) {
                return@withContext LoginResponse(
                    success = true,
                    message = "Success",
                    statusCode = statusCode
                )
            }

            return@withContext try {
                val responseJson = connection.inputStream.bufferedReader().use {
                    JSONObject(it.readText())
                }
                LoginResponse(
                    success = responseJson.getBoolean("success"),
                    message = responseJson.getString("message"),
                    statusCode = statusCode
                )
            } catch (e: JSONException) {
                LoginResponse(
                    success = false,
                    message = "Invalid response format",
                    statusCode = statusCode
                )
            }
        } catch (e: IOException) {
            LoginResponse(
                success = false,
                message = "Request could not be made",
                statusCode = -1
            )
        }
    }
}