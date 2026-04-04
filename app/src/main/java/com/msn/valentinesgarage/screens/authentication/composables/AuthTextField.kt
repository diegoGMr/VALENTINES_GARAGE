package com.msn.valentinesgarage.screens.authentication.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
) {
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
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = AppColors.White, // for now
            unfocusedContainerColor = AppColors.White, // for now
            focusedBorderColor      = AppColors.Orange,
            unfocusedBorderColor    = AppColors.LightGray,
            cursorColor             = AppColors.Orange,
            focusedTextColor        = AppColors.FontBlackMedium,
            unfocusedTextColor      = AppColors.FontBlackMedium,
            focusedPlaceholderColor = AppColors.TextHint,
            unfocusedPlaceholderColor = AppColors.TextHint,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthTextField() {
    var text by remember { mutableStateOf("") }
    AuthTextField(value = text, onValueChange = { text = it }, placeholder = "Email")
}