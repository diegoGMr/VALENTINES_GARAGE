package com.msn.valentinesgarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.msn.valentinesgarage.activities.authenticationActivity.LoginActivity
import com.msn.valentinesgarage.activities.authenticationActivity.SignUpActivity
import com.msn.valentinesgarage.activities.homeActivity.HomeActivity
import com.msn.valentinesgarage.theme.ValentinesGarageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValentinesGarageTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentScreen by remember { mutableStateOf("login") }
                    var authToken by remember { mutableStateOf<String?>(null) }
                    var userId by remember { mutableStateOf<Int?>(null) }
                    var userRole by remember { mutableStateOf<String?>(null) }

                    when (currentScreen) {
                        "home" -> HomeActivity(
                            modifier = Modifier.fillMaxSize(),
                            token = authToken ?: "",
                            role = userRole ?: "mechanic",
                            onLogout = {
                                authToken = null
                                userId = null
                                userRole = null
                                currentScreen = "login"
                            },
                        )
                        "signup" -> SignUpActivity(onLogin = { currentScreen = "login" })
                        "login" -> LoginActivity(
                            onLoginSuccess = { token, id, role ->
                                authToken = token
                                userId = id
                                userRole = role
                                currentScreen = "home"
                            },
                            onCreateAccount = { currentScreen = "signup" },
                        )
                    }
                }
            }
        }
    }
}
