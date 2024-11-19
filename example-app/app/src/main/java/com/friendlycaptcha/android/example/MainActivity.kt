package com.friendlycaptcha.android.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.friendlycaptcha.android.example.ui.theme.FriendlyCaptchaExampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.friendlycaptcha.android.sdk.*

// Note: you can use this example sitekey if you only want to see the clientside behavior, but
// you will not be able to see it work end-to-end (as you won't be able to verify the response on
// the server).
//
// In other words: replace this with your own sitekey when you want to test the full flow.
const val FRIENDLY_CAPTCHA_SITEKEY = "FCMGD7SIQS6JTVKU"

class MainActivity : ComponentActivity() {
    private val sdk by lazy {
        FriendlyCaptchaSDK(context = this, apiEndpoint = "global")
    }

    private val widget by lazy {
        sdk.createWidget(sitekey = FRIENDLY_CAPTCHA_SITEKEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            FriendlyCaptchaExampleAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LoginForm(
                        modifier = Modifier.padding(innerPadding),
                        onLoginClicked = { username, password, captchaResponse, setLoading, setMessage ->
                            setLoading(true)
                            setMessage("")

                            CoroutineScope(Dispatchers.Main).launch {
                                val response = doLoginRequest(username, password, captchaResponse)
                                setLoading(false)
                                if (response.success) {
                                    startActivity(
                                        Intent(
                                            this@MainActivity,
                                            SuccessActivity::class.java
                                        )
                                    )
                                    finish()
                                } else {
                                    // We must reset the widget after a failed attempt,
                                    // the captcha response can only be used once.
                                    widget.reset()
                                    setMessage(response.message)
                                }
                            }
                        },
                        widget = widget
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    onLoginClicked: (
        username: String,
        password: String,
        captchaResponse: String,
        setLoading: (Boolean) -> Unit,
        setMessage: (String) -> Unit
    ) -> Unit,
    widget: FriendlyCaptchaWidgetHandle
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val captchaResponse = remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var buttonEnabled by remember { mutableStateOf(false) }

    widget.setOnStateChangeListener { event ->
        captchaResponse.value = event.response

        when (event.state) {
            "reset" -> buttonEnabled = false
            "completed" -> buttonEnabled = true
            // The user will be able to restart the widget by clicking it.
            "expired" -> buttonEnabled = false
            // We enable the button on errors too, if Friendly Captcha is misbehaving (i.e. it's offline),
            // the user can still submit the form (albeit without a valid captcha response).
            "error" -> buttonEnabled = true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // UI improvement: start the widget as soon as the field is focused.
                    if (focusState.isFocused) {
                        widget.start()
                    }
                }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(8.dp),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        widget.start()
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            factory = { _ ->
                widget.view
            },
            // By default the widget height will grow to any size, 72dp is a good default.
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                onLoginClicked(
                    username.value,
                    password.value,
                    captchaResponse.value,
                    { isLoading = it },
                    { loginMessage = it }
                )
            },
            enabled = !isLoading && buttonEnabled,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3DDC84)),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Click to log in")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This is an example app.\nYou can enter any username or password.",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(loginMessage, color = Color.Red)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginFormPreview() {
    FriendlyCaptchaExampleAppTheme {
        LoginForm(
            onLoginClicked = { _, _, _, _, _ -> },
            widget = FriendlyCaptchaSDK(LocalContext.current).createWidget(sitekey = FRIENDLY_CAPTCHA_SITEKEY),
        )
    }
}