package com.msn.valentinesgarage.ui.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msn.valentinesgarage.ui.auth.BackgroundWhite
import com.msn.valentinesgarage.ui.auth.components.*

@Composable
fun CreateAccountScreen(
    onSignupClick: (email: String, password: String, confirmPassword: String) -> Unit = { _, _, _ -> },
    onLogin: () -> Unit = {},
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite),
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

            AuthScreenTitle(text = "Create your account")

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

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm Password",
                isPassword = true,
            )

            Spacer(modifier = Modifier.height(20.dp))

            AuthPrimaryButton(
                text = "SIGNUP",
                onClick = { onSignupClick(email, password, confirmPassword) },
            )
        }

        AuthFooterLink(
            prompt = "Already have an account?  ",
            linkText = "login",
            onLinkClick = onLogin,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun PreviewCreateAccountScreen() {
    CreateAccountScreen()
}