package com.msn.valentinesgarage.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.screens.authentication.composables.*
import com.msn.valentinesgarage.screens.authentication.viewmodels.LoginViewModel
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun LoginScreen(
    onLoginSuccess: (token: String, userId: Int, role: String) -> Unit = { _, _, _ -> },
    onCreateAccount: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val uiState by loginViewModel.uiState.collectAsState()

    // Navigate on success
    LaunchedEffect(uiState.token) {
        if (uiState.token != null) {
            onLoginSuccess(uiState.token!!, uiState.userId!!, uiState.role!!)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            BrandLogo()
            Spacer(modifier = Modifier.height(52.dp))
            AuthScreenTitle(text = "Login to your account")
            Spacer(modifier = Modifier.height(20.dp))
            AuthTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(14.dp))
            AuthTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true,
            )
            // Error message
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error!!,
                    color = AppColors.Red,
                    fontSize = 13.sp,
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            AuthPrimaryButton(
                text = if (uiState.isLoading) "LOGGING IN..." else "LOGIN",
                onClick = {
                    if (!uiState.isLoading) loginViewModel.login(email, password)
                },
            )
        }
        AuthFooterLink(
            prompt = "Don't have an account?  ",
            linkText = "create account",
            onLinkClick = onCreateAccount,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}
