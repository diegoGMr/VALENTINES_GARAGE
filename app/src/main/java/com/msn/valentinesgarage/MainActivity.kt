package com.msn.valentinesgarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.msn.valentinesgarage.data.SessionManager
import com.msn.valentinesgarage.screens.authentication.LoginScreen
import com.msn.valentinesgarage.screens.authentication.SignUpScreen
import com.msn.valentinesgarage.screens.dialog.FullLoadingScreen
import com.msn.valentinesgarage.screens.home.HomeScreen
import com.msn.valentinesgarage.theme.ValentinesGarageTheme
import kotlinx.coroutines.delay

data class UserProfile(
    val token: String,
    val userId: Int,
    val role: String,
)

class MainActivity : ComponentActivity() {
    private val shouldCallLoginApi = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val sessionManager = SessionManager(this)
        
        enableEdgeToEdge()
        setContent {
            ValentinesGarageTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var isInitializing by remember { mutableStateOf(true) }
                    var currentScreen by remember { mutableStateOf("login") }
                    var userProfile by remember { mutableStateOf<UserProfile?>(null) }

                    LaunchedEffect(Unit) {
                        // Check session
                        if (sessionManager.isLoggedIn()) {
                            userProfile = UserProfile(
                                token = sessionManager.getToken() ?: "",
                                userId = sessionManager.getUserId(),
                                role = sessionManager.getRole() ?: "client"
                            )
                            currentScreen = "home"
                        }
                        delay(500) // Small delay for smooth transition
                        isInitializing = false
                    }

                    if (isInitializing) {
                        FullLoadingScreen(message = "Valentines Garage")
                    } else {
                        when (currentScreen) {
                            "home" -> {
                                userProfile?.let { profile ->
                                    HomeScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        token = profile.token,
                                        userId = profile.userId,
                                        role = profile.role,
                                        onLogout = {
                                            sessionManager.clearSession()
                                            userProfile = null
                                            currentScreen = "login"
                                        }
                                    )
                                } ?: run {
                                    currentScreen = "login"
                                }
                            }
                            "signup" -> {
                                SignUpScreen(
                                    onLogin = { currentScreen = "login" },
                                    onSignUpSuccess = { token, id, role ->
                                        sessionManager.saveSession(token, id, role)
                                        userProfile = UserProfile(token = token, userId = id, role = role)
                                        currentScreen = "home"
                                    }
                                )
                            }
                            "login" -> {
                                LoginScreen(
                                    shouldCallLoginApi = shouldCallLoginApi,
                                    onLoginSuccess = { token, id, role ->
                                        sessionManager.saveSession(token, id, role)
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
}
