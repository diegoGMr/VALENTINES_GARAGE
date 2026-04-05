package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors
@Composable
fun WeeklyCalendar(
    dayNumbers: List<Int>,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val dayLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dayNumbers.forEachIndexed { index, dayNumber ->
            val selected = dayNumber == selectedDay
            val label = dayLabels[index % dayLabels.size]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onDaySelected(dayNumber) },
            ) {
                Text(
                    text = label,
                    color = AppColors.TextHint,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(34.dp)
                        .background(
                            color = if (selected) AppColors.NearBlack else AppColors.White,
                            shape = CircleShape,
                        )
                        .border(
                            width = 1.dp,
                            color = if (selected) AppColors.NearBlack else AppColors.LightGray,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = dayNumber.toString(),
                        color = if (selected) AppColors.White else AppColors.TextHint,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWeeklyCalendar() {
    WeeklyCalendar(
        dayNumbers = listOf(22, 23, 24, 25, 26, 27, 28),
        selectedDay = 24,
    )
}
