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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.friendlycaptcha.android.example.ui.theme.FriendlyCaptchaExampleAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
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
                                    setMessage(response.message)
                                }
                            }
                        }
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
    ) -> Unit
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val captchaResponse = remember { mutableStateOf("") }
    var loginMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        // Placeholder for captcha widget. Replace with the actual widget.
        Text("Captcha Widget Placeholder", modifier = Modifier.fillMaxWidth())
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
            enabled = !isLoading,
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
        LoginForm(onLoginClicked = { _, _, _, _, _ -> })
    }
}