package com.msn.valentinesgarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.data.SessionManager
import com.msn.valentinesgarage.screens.authentication.LoginScreen
import com.msn.valentinesgarage.screens.authentication.SignUpScreen
import com.msn.valentinesgarage.screens.dialog.FullLoadingScreen
import com.msn.valentinesgarage.screens.home.HomeScreen
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ValentinesGarageTheme
import kotlinx.coroutines.delay

data class UserProfile(
    val token: String,
    val userId: Int,
    val role: String,
)

@Composable
private fun LogoutAnimation(onAnimationEnd: () -> Unit) {
    var visible by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 350),
        label = "logout_fade"
    )
    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logout_scale"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400, delayMillis = 250),
        label = "logout_text_fade"
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(1600)
        onAnimationEnd()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha)
            .background(AppColors.Orange),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier
                    .size(90.dp)
                    .scale(logoScale)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.applogo),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp, 48.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "Logging out...",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}

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
                    var isLoggingOut by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        if (sessionManager.isLoggedIn()) {
                            userProfile = UserProfile(
                                token = sessionManager.getToken() ?: "",
                                userId = sessionManager.getUserId(),
                                role = sessionManager.getRole() ?: "client"
                            )
                            currentScreen = "home"
                        }
                        delay(500)
                        isInitializing = false
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
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
                                            onLogout = { isLoggingOut = true }
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

                        if (isLoggingOut) {
                            LogoutAnimation(
                                onAnimationEnd = {
                                    sessionManager.clearSession()
                                    userProfile = null
                                    isLoggingOut = false
                                    currentScreen = "login"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
