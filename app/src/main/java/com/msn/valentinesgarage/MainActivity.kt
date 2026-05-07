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
import com.msn.valentinesgarage.screens.authentication.LoginScreen
import com.msn.valentinesgarage.screens.authentication.SignUpScreen
import com.msn.valentinesgarage.screens.home.HomeScreen
import com.msn.valentinesgarage.theme.ValentinesGarageTheme

data class UserProfile(
    val token: String,
    val userId: Int,
    val role: String,
)

class MainActivity : ComponentActivity() {
    private val shouldCallLoginApi = true // Set to true to enable API calls for login

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValentinesGarageTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var currentScreen by remember { mutableStateOf("login") }
                    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

                    when (currentScreen) {
                        "home" -> {
                            userProfile?.let { profile ->
                                HomeScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    token = profile.token,
                                    role = profile.role,
                                    onLogout = {
                                        userProfile = null
                                        currentScreen = "login"
                                    }
                                )
                            } ?: run {
                                currentScreen = "login"
                            }
                        }
                        "signup" -> {
                            SignUpScreen(onLogin = { currentScreen = "login" })
                        }
                        "login" -> {
                            LoginScreen(
                                shouldCallLoginApi = shouldCallLoginApi,
                                onLoginSuccess = { token, id, role ->
                                    userProfile = UserProfile(token = token, userId = id, role = role)
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
}
