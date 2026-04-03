package com.msn.valentinesgarage.activities.authenticationActivity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msn.valentinesgarage.activities.authenticationActivity.composables.AuthFooterLink
import com.msn.valentinesgarage.activities.authenticationActivity.composables.AuthPrimaryButton
import com.msn.valentinesgarage.activities.authenticationActivity.composables.AuthScreenTitle
import com.msn.valentinesgarage.activities.authenticationActivity.composables.AuthTextField
import com.msn.valentinesgarage.activities.authenticationActivity.composables.BrandLogo
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun LoginActivity(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onCreateAccount: () -> Unit = {},
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

            Spacer(modifier = Modifier.height(36.dp))

            AuthPrimaryButton(
                text = "LOGIN",
                onClick = { onLoginClick(email, password) },
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
fun PreviewLoginActivity() {
    LoginActivity()
}


