package com.msn.valentinesgarage.screens.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Info

sealed class DialogType {
    data class Success(val title: String, val message: String) : DialogType()
    data class Error(val title: String, val message: String) : DialogType()
    data class Information(val title: String, val message: String) : DialogType()
    object Loading : DialogType()
}

@Composable
fun DialogScreen(
    dialogType: DialogType,
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        when (dialogType) {
            is DialogType.Success -> SuccessCard(
                title = dialogType.title,
                message = dialogType.message,
                onOk = onDismiss
            )
            is DialogType.Error -> ErrorCard(
                title = dialogType.title,
                message = dialogType.message,
                onOk = onDismiss
            )
            is DialogType.Information -> InformationCard(
                title = dialogType.title,
                message = dialogType.message,
                onOk = onDismiss
            )
            is DialogType.Loading -> LoadingCard()
        }
    }
}

@Composable
fun SuccessCard(
    title: String,
    message: String,
    onOk: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .background(
                color = AppColors.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Check,
            contentDescription = "Success",
            modifier = Modifier.size(48.dp),
            tint = AppColors.Green
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.FontBlackStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = AppColors.FontBlackMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOk,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Green
            )
        ) {
            Text(
                text = "OK",
                color = AppColors.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun ErrorCard(
    title: String,
    message: String,
    onOk: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .background(
                color = AppColors.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = AppColors.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.FontBlackStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = AppColors.FontBlackMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOk,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Red
            )
        ) {
            Text(
                text = "OK",
                color = AppColors.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun InformationCard(
    title: String,
    message: String,
    onOk: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .background(
                color = AppColors.White,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = FontAwesomeIcons.Solid.Info,
            contentDescription = "Information",
            modifier = Modifier.size(48.dp),
            tint = AppColors.Orange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.FontBlackStrong
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            fontSize = 14.sp,
            color = AppColors.FontBlackMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onOk,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Orange
            )
        ) {
            Text(
                text = "OK",
                color = AppColors.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun LoadingCard() {
    Column(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp)
            .background(
                color = AppColors.White,
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = AppColors.Orange,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSuccessDialog() {
    DialogScreen(DialogType.Success("Success", "Login successful!"))
}

@Preview(showBackground = true)
@Composable
fun PreviewErrorDialog() {
    DialogScreen(DialogType.Error("Error", "Invalid email or password"))
}

@Preview(showBackground = true)
@Composable
fun PreviewInformationDialog() {
    DialogScreen(DialogType.Information("Information", "Please check your credentials"))
}

@Preview(showBackground = true)
@Composable
fun PreviewLoadingDialog() {
    DialogScreen(DialogType.Loading)
}
