package com.msn.valentinesgarage.screens.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.screens.authentication.composables.AuthFooterLink
import com.msn.valentinesgarage.screens.authentication.composables.AuthPrimaryButton
import com.msn.valentinesgarage.screens.authentication.composables.AuthScreenTitle
import com.msn.valentinesgarage.screens.authentication.composables.AuthTextField
import com.msn.valentinesgarage.screens.authentication.viewmodels.SignUpViewModel
import com.msn.valentinesgarage.screens.dialog.DialogScreen
import com.msn.valentinesgarage.screens.dialog.DialogType
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun SignUpScreen(
    onLogin: () -> Unit = {},
    onSignUpSuccess: (token: String, userId: Int, role: String) -> Unit = { _, _, _ -> },
    signUpViewModel: SignUpViewModel = viewModel(),
) {
    val uiState by signUpViewModel.uiState.collectAsState()
    val fullNameError = signUpViewModel.fullNameError()
    val emailError = signUpViewModel.emailError()
    val phoneError = signUpViewModel.phoneError()
    val passwordError = signUpViewModel.passwordError()
    val confirmError = signUpViewModel.confirmPasswordError()
    val termsError = if (uiState.hasSubmitted && !uiState.agreedToTerms) "Please agree to terms and conditions" else null

    LaunchedEffect(uiState.token) {
        val token = uiState.token
        val id = uiState.userId
        val role = uiState.role
        if (token != null && id != null && role != null) {
            // Give the user a moment to see the success dialog if we want,
            // but the prompt says "automatically" so we navigate on dismiss below.
        }
    }

    val showTermsScreen = remember { mutableStateOf(false) }

    if (showTermsScreen.value) {
        TermsConditionsScreen(
            onBack = { showTermsScreen.value = false },
            onAgree = {
                signUpViewModel.onAgreedToTermsChanged(true)
                showTermsScreen.value = false
            },
        )
        return
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
            modifier = Modifier.fillMaxWidth()
                .align(Alignment.BottomCenter)
                .fillMaxHeight(0.85f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                AuthScreenTitle(text = "Create your account")
                Spacer(modifier = Modifier.height(40.dp))

                AuthTextField(
                    value = uiState.fullName,
                    onValueChange = signUpViewModel::onFullNameChanged,
                    onFocusChanged = signUpViewModel::onFullNameFocusChanged,
                    placeholder = "Full name",
                    isError = fullNameError != null,
                    errorText = fullNameError,
                )

                Spacer(modifier = Modifier.height(10.dp))

                AuthTextField(
                    value = uiState.email,
                    onValueChange = signUpViewModel::onEmailChanged,
                    onFocusChanged = signUpViewModel::onEmailFocusChanged,
                    placeholder = "Email",
                    keyboardType = KeyboardType.Email,
                    isError = emailError != null,
                    errorText = emailError,
                )

                Spacer(modifier = Modifier.height(10.dp))

                AuthTextField(
                    value = uiState.phone,
                    onValueChange = signUpViewModel::onPhoneChanged,
                    onFocusChanged = signUpViewModel::onPhoneFocusChanged,
                    placeholder = "Phone number (10 digits)",
                    keyboardType = KeyboardType.Phone,
                    isError = phoneError != null,
                    errorText = phoneError,
                )

                Spacer(modifier = Modifier.height(10.dp))

                AuthTextField(
                    value = uiState.password,
                    onValueChange = signUpViewModel::onPasswordChanged,
                    onFocusChanged = signUpViewModel::onPasswordFocusChanged,
                    placeholder = "Password",
                    isPassword = true,
                    isError = passwordError != null,
                    errorText = passwordError,
                )

                Spacer(modifier = Modifier.height(10.dp))

                AuthTextField(
                    value = uiState.confirmPassword,
                    onValueChange = signUpViewModel::onConfirmPasswordChanged,
                    onFocusChanged = signUpViewModel::onConfirmPasswordFocusChanged,
                    placeholder = "Confirm Password",
                    isPassword = true,
                    isError = confirmError != null,
                    errorText = confirmError,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(
                        checked = uiState.agreedToTerms,
                        onCheckedChange = { signUpViewModel.onAgreedToTermsChanged(it) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AppColors.Orange,
                            uncheckedColor = AppColors.LightGray,
                            checkmarkColor = AppColors.White,
                        ),
                    )
                    Text(
                        text = "I agree to Terms & Conditions",
                        fontSize = 13.sp,
                        color = AppColors.FontBlackMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showTermsScreen.value = true },
                    )
                }

                if (termsError != null) {
                    Text(
                        text = termsError,
                        color = AppColors.Red,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp),
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                AuthPrimaryButton(
                    text = if (uiState.isLoading) "SIGNING UP..." else "SIGNUP",
                        isEnabled = !uiState.isLoading && signUpViewModel.isFormValid(),
                    onClick = {
                            if (!uiState.isLoading && signUpViewModel.isFormValid()) {
                            signUpViewModel.register()
                        }
                    },
                )

                Spacer(modifier = Modifier.weight(1f))

                AuthFooterLink(
                    prompt = "Already have an account?  ",
                    linkText = "login",
                    onLinkClick = onLogin,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }


        uiState.activeDialog?.let { dialogType ->
            DialogScreen(
                dialogType = dialogType,
                onDismiss = {
                    val token = uiState.token
                    val id = uiState.userId
                    val role = uiState.role
                    
                    signUpViewModel.dismissDialog()
                    if (dialogType is DialogType.Success && token != null && id != null && role != null) {
                        signUpViewModel.resetState()
                        onSignUpSuccess(token, id, role)
                    }
                },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
fun PreviewCreateAccountScreen() {
    SignUpScreen()
}
