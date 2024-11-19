package com.friendlycaptcha.android.example

import android.util.Log
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
// This is the URL of the server that will handle the login request.
// 10.0.2.2 is the alias to your host loopback interface (i.e., localhost) in the Android Emulator.
const val LOGIN_ENDPOINT_URL = "http://10.0.2.2:3600/login"

suspend fun doLoginRequest(username: String, password: String, captchaResponse: String): LoginResponse {
    // Make sure you are running the server locally before running this code (see README.md and the
    // server folder).
    val url = URL(LOGIN_ENDPOINT_URL)
    val json = JSONObject().apply {
        put("username", username)
        put("password", password)
        put("frc-captcha-response", captchaResponse)
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
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                LoginResponse(
                    success = responseJson.getBoolean("success"),
                    message = responseJson.getString("message"),
                    statusCode = statusCode
                )
            } catch (e: JSONException) {
                Log.e("example-app", "JSON Exception in parsing response", e)
                LoginResponse(
                    success = false,
                    message = "Invalid response format",
                    statusCode = statusCode
                )
            }
        } catch (e: IOException) {
            LoginResponse(
                success = false,
                message = "Request could not be made, are you running the server?",
                statusCode = -1
            )
        }
    }
}