package com.friendlycaptcha.android.debugapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.friendlycaptcha.android.sdk.FriendlyCaptchaSDK
import com.friendlycaptcha.android.sdk.FriendlyCaptchaWidgetHandle

class MainActivity : AppCompatActivity() {

    private lateinit var stateTextView: TextView
    private lateinit var responseTextView: TextView
    private lateinit var widgetHandle: FriendlyCaptchaWidgetHandle
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val mount = findViewById<FrameLayout>(R.id.mount)
        stateTextView = findViewById<TextView>(R.id.stateTextView)
        responseTextView = findViewById<TextView>(R.id.responseTextView)
        val inputField = findViewById<EditText>(R.id.inputField)
        submitButton = findViewById<Button>(R.id.submitButton)


        val sdk = FriendlyCaptchaSDK(context = this, apiEndpoint = "global")
        val sitekey = "FCMGD7SIQS6JTVKU" // Not a secret key, you can test with it (but you won't be able to verify the response).

        widgetHandle = sdk.createWidget(sitekey)

        // Disable the submit button initially
        submitButton.isEnabled = false

        widgetHandle.setOnStateChangeListener { event ->
            runOnUiThread {
                stateTextView.text = event.state
                responseTextView.text = event.response
            }
        }

        // We could also check for event.state == "complete" in the `onStateChangeListener` above, but let's exercise the different listeners below.
        widgetHandle.setOnCompleteListener { _ ->
            runOnUiThread {
                submitButton.isEnabled = true
            }
        }

        widgetHandle.setOnErrorListener { _ ->
            runOnUiThread {
                submitButton.isEnabled = true
            }
        }

        widgetHandle.setOnExpireListener { _ ->
            runOnUiThread {
                submitButton.isEnabled = false
            }
        }

        inputField.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                widgetHandle.start()
            }
        }

        mount.addView(widgetHandle.view)

        // When submit button is pressed, we reset the widget
        submitButton.setOnClickListener {
            widgetHandle.reset()
            submitButton.isEnabled = false
        }
    }
}