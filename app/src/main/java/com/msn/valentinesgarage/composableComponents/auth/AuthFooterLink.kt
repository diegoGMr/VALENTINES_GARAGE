package com.msn.valentinesgarage.composableComponents.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun AuthFooterLink(
    prompt: String,
    linkText: String,
    onLinkClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = prompt,
            color = AppColors.FontBlackMedium,
            fontSize = 14.sp,
        )
        TextButton(
            onClick = onLinkClick,
            contentPadding = PaddingValues(0.dp),
        ) {
            Text(
                text = linkText,
                color = AppColors.FontBlackMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAuthFooterLink() {
    AuthFooterLink(
        prompt = "Don't have an account?  ",
        linkText = "create account",
        onLinkClick = {},
    )
}