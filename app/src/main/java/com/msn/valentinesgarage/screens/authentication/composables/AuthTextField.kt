package com.msn.valentinesgarage.screens.authentication.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorText: String? = null,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    var isFocused by remember { mutableStateOf(false) }
    var hasBeenFocused by remember { mutableStateOf(false) }
    val showError = isError && !isFocused

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = AppColors.TextHint,
                fontSize = 15.sp,
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        // Keep focused styling in brand color; show error only when field is unfocused.
        isError = showError,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = AppColors.White,
            unfocusedContainerColor = AppColors.White,
            focusedBorderColor = AppColors.Orange,
            unfocusedBorderColor = if (showError) AppColors.Red else AppColors.LightGray,
            errorBorderColor = AppColors.Red,
            cursorColor = AppColors.Orange,
            focusedTextColor = AppColors.FontBlackMedium,
            unfocusedTextColor = AppColors.FontBlackMedium,
            focusedPlaceholderColor = AppColors.TextHint,
            unfocusedPlaceholderColor = AppColors.TextHint,
            errorTextColor = AppColors.Red,
        ),
        supportingText = {
            if (showError && !errorText.isNullOrBlank()) {
                Text(
                    text = errorText,
                    color = AppColors.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged {
                val nowFocused = it.isFocused
                if (nowFocused) {
                    hasBeenFocused = true
                } else if (hasBeenFocused && isFocused) {
                    // Trigger touched state only after a real focus -> unfocus interaction.
                    onFocusChanged(false)
                }
                isFocused = nowFocused
            },
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthTextField() {
    AuthTextField(value = "", onValueChange = {}, placeholder = "Email")
}