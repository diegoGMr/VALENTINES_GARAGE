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
                        "home" -> HomeScreen(modifier = Modifier.fillMaxSize())
                        "signup" -> SignUpScreen(onLogin = { currentScreen = "login" })
                        "login" -> LoginScreen(
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
