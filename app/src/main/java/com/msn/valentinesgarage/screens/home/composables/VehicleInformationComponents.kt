package com.msn.valentinesgarage.screens.home.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.theme.AppColors
import coil.compose.AsyncImage

@Composable
fun InformationCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, AppColors.LightGray.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .background(AppColors.LightGray.copy(alpha = 0.24f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = AppColors.FontBlackSoft,
            fontSize = 12.sp,
        )
        Text(
            text = value,
            color = AppColors.FontBlackStrong,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
fun MechanicCard(
    @DrawableRes imageRes: Int,
    fullName: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .size(width = 104.dp, height = 92.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, AppColors.LightGray.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .background(AppColors.LightGray.copy(alpha = 0.2f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = fullName,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape),
        )
        Text(
            text = fullName,
            maxLines = 1,
            color = AppColors.FontBlackStrong,
            fontSize = 11.sp,
        )
    }
}

@Composable
fun SectionHeaderRow(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = AppColors.FontBlackStrong,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        )

        if (actionText != null) {
            Text(
                text = actionText,
                color = AppColors.Orange,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick?.invoke() },
            )
        }
    }
}

@Composable
fun IssueTaskCard(
    imageUrl: String,
    title: String,
    subtitle: String,
    taskCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.LightGray.copy(alpha = 0.8f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightGray.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )

            Text(
                text = title,
                color = AppColors.FontBlackStrong,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Text(
                text = subtitle,
                color = AppColors.FontBlackSoft,
                fontSize = 12.sp,
            )

            LabelChip(
                text = "$taskCount tasks",
                background = Color(0xFFE8EDF7),
                textColor = AppColors.FontBlackSoft,
            )
        }
    }
}

@Composable
fun MoreIssuesCard(
    remainingCount: Int,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AppColors.LightGray.copy(alpha = 0.8f), RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightGray.copy(alpha = 0.12f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(248.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+$remainingCount issues",
                color = AppColors.TextHint,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
private fun LabelChip(
    text: String,
    background: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationCardPreview() {
    InformationCard(title = "Mileage", value = "12,024 km", modifier = Modifier.fillMaxWidth())
}

@Preview(showBackground = true)
@Composable
private fun MechanicCardPreview() {
    MechanicCard(imageRes = R.drawable.defaultprofileicon, fullName = "Robert Mount")
}

@Preview(showBackground = true)
@Composable
private fun IssueTaskCardPreview() {
    IssueTaskCard(
        imageUrl = "https://i.pinimg.com/1200x/f5/ff/28/f5ff28479532dafbc506ba7bcf3ff40d.jpg",
        title = "Engine warning light",
        subtitle = "Diagnostic required before next route",
        taskCount = 2,
    )
}


