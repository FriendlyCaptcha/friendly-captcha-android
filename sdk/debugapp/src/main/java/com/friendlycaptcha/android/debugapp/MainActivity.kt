package com.friendlycaptcha.android.debugapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.friendlycaptcha.android.sdk.FriendlyCaptchaSDK

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val layout = findViewById<ConstraintLayout>(R.id.main)

        val sdk = FriendlyCaptchaSDK(context = this, apiEndpoint = "global")
        val sitekey = "FCMGD7SIQS6JTVKU"


        val captchaWidget = sdk.createWidget(sitekey)
        layout.addView(captchaWidget.webView)

        Log.i("FriendlyCaptcha", "Created widget and loaded test URL")
    }
}