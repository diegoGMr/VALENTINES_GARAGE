package com.msn.valentinesgarage.activities.homeActivity.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun HomeVehicleTaskCard(
    imageUrl: String,
    title: String,
    subtitle: String,
    dateText: String,
    pendingTasksText: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    //PARAMS TO BE REPLACED WITH DATA CLASS

    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else {
        modifier
    }

    Card(
        modifier = cardModifier
            .fillMaxWidth()
            .border(1.dp, AppColors.LightGray.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .height(126.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.LightGray.copy(alpha = 0.22f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 138.dp, height = 102.dp)
                    .clip(RoundedCornerShape(12.dp)),
            )

            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = dateText,
                    fontSize = 10.sp,
                    color = AppColors.TextHint,
                    modifier = Modifier.align(Alignment.Start),
                )

                Text(
                    text = title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.FontBlackStrong,
                    lineHeight = 30.sp,
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = AppColors.FontBlackSoft,
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.Orange)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = pendingTasksText,
                        fontSize = 13.sp,
                        color = AppColors.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeVehicleTaskCardPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        HomeVehicleTaskCard(
            imageUrl = "https://images.unsplash.com/photo-1601584115197-04ecc0da31d7?w=400",
            title = "HSD343",
            subtitle = "Scania Railer",
            dateText = "18 March 2026 12:07am",
            pendingTasksText = "19 pending tasks",
        )
    }
}
