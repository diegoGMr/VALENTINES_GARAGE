package com.msn.valentinesgarage.activities.authenticationActivity.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun AuthScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = AppColors.FontBlackStrong,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.fillMaxWidth(),
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthScreenTitle() {
    AuthScreenTitle(text = "Login to your account")
}