package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors
@Composable
fun TaskStatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier,
) {
    val (bg, fg) = statusColors(status)

    Text(
        text = status.label,
        color = fg,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    )
}

private fun statusColors(status: TaskStatus): Pair<Color, Color> = when (status) {
    TaskStatus.Open          -> AppColors.OrangeWhite to AppColors.Orange
    TaskStatus.Assigned      -> AppColors.Mint.copy(alpha = 0.25f) to AppColors.Green
    TaskStatus.InProgress    -> AppColors.Orange.copy(alpha = 0.15f) to AppColors.Orange
    TaskStatus.PendingReview -> AppColors.LightGray to AppColors.FontBlackMedium
    TaskStatus.PendingParts  -> AppColors.Pink.copy(alpha = 0.20f) to AppColors.Red
    TaskStatus.Completed     -> AppColors.Mint.copy(alpha = 0.30f) to AppColors.Green
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskStatusBadge() {
    TaskStatusBadge(status = TaskStatus.InProgress)
}
