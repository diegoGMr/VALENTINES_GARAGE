package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CheckCircle

data class TaskCategoryUi(
    val name: String,
    val count: Int,
)

data class TaskUi(
    val issueTitle: String,
    val vehicleName: String,
    val time: String,
    val status: String,
    val actionText: String? = null,
)

@Composable
fun WeeklyCalendar(
    dayNumbers: List<Int>,
    currentDay: Int,
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
            val selected = dayNumber == currentDay
            val label = dayLabels[index % dayLabels.size]

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        modifier = modifier.padding(bottom = 4.dp),
        color = AppColors.FontBlackStrong,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun CategoryCard(
    category: TaskCategoryUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = category.name,
                color = AppColors.FontBlackMedium,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "${category.count} tasks",
                color = AppColors.TextHint,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
fun TaskCard(
    task: TaskUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = task.issueTitle,
                color = AppColors.FontBlackStrong,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Vehicle: ${task.vehicleName}",
                color = AppColors.FontBlackSoft,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = task.time,
                color = AppColors.TextHint,
                fontSize = 12.sp,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = task.status,
                    color = AppColors.Orange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
                if (task.actionText != null) {
                    Text(
                        text = task.actionText,
                        color = AppColors.Green,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
fun CompletedTaskCard(
    task: TaskUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Mint.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.CheckCircle,
                contentDescription = "Completed",
                tint = AppColors.Green,
                modifier = Modifier.size(16.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = task.issueTitle,
                    color = AppColors.FontBlackStrong,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Vehicle: ${task.vehicleName}",
                    color = AppColors.FontBlackSoft,
                    fontSize = 12.sp,
                )
                Text(
                    text = "Completed at ${task.time}",
                    color = AppColors.TextHint,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

