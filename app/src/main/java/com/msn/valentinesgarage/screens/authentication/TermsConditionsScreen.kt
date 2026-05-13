package com.msn.valentinesgarage.screens.authentication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.screens.authentication.composables.AuthPrimaryButton
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft

@Composable
fun TermsConditionsScreen(
    onBack: () -> Unit,
    onAgree: () -> Unit,
) {
    ConfigureSystemBars(statusBarColor = AppColors.White)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.White)
            .topSafeDrawingPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onBack() }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.ChevronLeft,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp),
                tint = AppColors.FontBlackStrong,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Terms & Conditions",
                color = AppColors.FontBlackStrong,
                fontSize = 18.sp,
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = buildString {
                append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. ")
                append("Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. ")
                append("Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. ")
                append("Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.\n\n")
                append("By tapping Agree, you confirm that you have read and accepted these terms regarding account registration, ")
                append("privacy handling, and platform usage responsibilities. You also acknowledge that these terms may be updated over time.\n\n")
                append("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. ")
                append("Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. ")
                append("Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. ")
                append("Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sodales ligula in libero.\n\n")
                append("Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. ")
                append("Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. ")
                append("Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet.")
            },
            color = AppColors.FontBlackMedium,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))

        AuthPrimaryButton(
            text = "AGREE",
            onClick = onAgree,
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PreviewTermsConditionsScreen() {
    TermsConditionsScreen(onBack = {}, onAgree = {})
}


