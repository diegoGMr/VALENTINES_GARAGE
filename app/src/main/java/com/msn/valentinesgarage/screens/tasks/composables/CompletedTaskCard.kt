package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.CheckCircle

@Composable
fun CompletedTaskCard(
    task: TaskUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.Mint.copy(alpha = 0.18f)),
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
                modifier = Modifier
                    .size(18.dp)
                    .padding(top = 2.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Struck-through title to signal completion visually
                Text(
                    text = task.issueTitle,
                    color = AppColors.FontBlackMedium,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = TextDecoration.LineThrough,
                )
                Text(
                    text = "Vehicle: ${task.vehicleName}",
                    color = AppColors.FontBlackSoft,
                    fontSize = 12.sp,
                )
                Text(
                    text = "Completed at ${task.completedAt ?: task.time}",
                    color = AppColors.TextHint,
                    fontSize = 12.sp,
                )
                if (task.notes.isNotBlank()) {
                    Text(
                        text = "Note: ${task.notes}",
                        color = AppColors.FontBlackMedium,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCompletedTaskCard() {
    CompletedTaskCard(
        task = TaskUi(
            id = "7",
            issueTitle = "Fuel filter replacement",
            vehicleName = "DAF XF – UYE901",
            time = "Yesterday 03:10 PM",
            category = "Maintenance",
            status = TaskStatus.Completed,
            completedAt = "Yesterday 03:10 PM",
            notes = "Replaced with OEM filter. Test drive ok.",
        ),
    )
}
