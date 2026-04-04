package com.msn.valentinesgarage.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.screens.authentication.composables.AuthFooterLink
import com.msn.valentinesgarage.screens.authentication.composables.AuthPrimaryButton
import com.msn.valentinesgarage.screens.authentication.composables.AuthScreenTitle
import com.msn.valentinesgarage.screens.authentication.composables.AuthTextField
import com.msn.valentinesgarage.screens.authentication.viewmodels.LoginViewModel
import com.msn.valentinesgarage.screens.dialog.DialogScreen
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun LoginScreen(
    onLoginSuccess: (token: String, userId: Int, role: String) -> Unit = { _, _, _ -> },
    onCreateAccount: () -> Unit = {},
    loginViewModel: LoginViewModel = viewModel(),
) {
    val uiState by loginViewModel.uiState.collectAsState()
    val emailError = loginViewModel.emailError()
    val passwordError = loginViewModel.passwordError()

    LaunchedEffect(uiState.token) {
        val token = uiState.token
        val id = uiState.userId
        val role = uiState.role
        if (token != null && id != null && role != null) {
            onLoginSuccess(token, id, role)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Orange),
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AppColors.White,
                modifier = Modifier.size(50.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = R.drawable.applogo),
                        contentDescription = "Brand Logo",
                        modifier = Modifier.size(56.dp, 44.dp),
                    )
                }
            }
        }

        Surface(
            color = AppColors.White,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.75f)
                .align(Alignment.BottomCenter),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 28.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                AuthScreenTitle(text = "Login to your account")
                Spacer(modifier = Modifier.height(40.dp))

                AuthTextField(
                    value = uiState.email,
                    onValueChange = loginViewModel::onEmailChanged,
                    onFocusChanged = loginViewModel::onEmailFocusChanged,
                    placeholder = "Email",
                    keyboardType = KeyboardType.Email,
                    isError = emailError != null,
                    errorText = emailError,
                )

                Spacer(modifier = Modifier.height(12.dp))

                AuthTextField(
                    value = uiState.password,
                    onValueChange = loginViewModel::onPasswordChanged,
                    onFocusChanged = loginViewModel::onPasswordFocusChanged,
                    placeholder = "Password",
                    isPassword = true,
                    isError = passwordError != null,
                    errorText = passwordError,
                )

                Spacer(modifier = Modifier.height(24.dp))

                AuthPrimaryButton(
                    text = if (uiState.isLoading) "LOGGING IN..." else "LOGIN",
                    isEnabled = !uiState.isLoading && loginViewModel.isFormValid(),
                    onClick = {
                        if (!uiState.isLoading && loginViewModel.isFormValid()) {
                            loginViewModel.login()
                        }
                    },
                )

                Spacer(modifier = Modifier.weight(1f))

                AuthFooterLink(
                    prompt = "Don't have an account? ",
                    linkText = "create account",
                    onLinkClick = onCreateAccount,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
        }

        uiState.activeDialog?.let { dialogType ->
            DialogScreen(dialogType = dialogType, onDismiss = loginViewModel::dismissDialog)
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun PreviewLoginScreen() {
    LoginScreen()
}
