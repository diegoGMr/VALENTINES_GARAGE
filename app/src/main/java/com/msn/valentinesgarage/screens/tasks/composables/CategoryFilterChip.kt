package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors
@Composable
fun CategoryFilterChip(
    category: TaskCategoryUi,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (isSelected) AppColors.NearBlack else AppColors.White
    val textColor = if (isSelected) AppColors.White else AppColors.FontBlackStrong
    val borderColor = if (isSelected) AppColors.NearBlack else AppColors.LightGray

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = category.name,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = "${category.count} tasks",
            color = if (isSelected) AppColors.LightGray else AppColors.TextHint,
            fontSize = 12.sp,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCategoryFilterChip() {
    CategoryFilterChip(
        category = TaskCategoryUi(name = "Maintenance", count = 4),
        isSelected = true,
        onClick = {},
    )
}
