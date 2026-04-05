package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Truck

@Composable
fun TaskCard(
    task: TaskUi,
    onTakeTask: (String) -> Unit = {},
    onAddNote: (String) -> Unit = {},
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = task.issueTitle,
                    color = AppColors.FontBlackStrong,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                )
                TaskStatusBadge(status = task.status)
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Truck,
                    contentDescription = null,
                    tint = AppColors.TextHint,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = task.vehicleName,
                    color = AppColors.FontBlackSoft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Clock,
                    contentDescription = null,
                    tint = AppColors.TextHint,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = task.time,
                    color = AppColors.TextHint,
                    fontSize = 12.sp,
                )
            }

            if (task.notes.isNotBlank()) {
                Text(
                    text = "Note: ${task.notes}",
                    color = AppColors.FontBlackMedium,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                )
            }

            when {
                task.status == TaskStatus.Open -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { onTakeTask(task.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.NearBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                    ) {
                        Text(
                            text = "Take Task",
                            color = AppColors.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                task.status == TaskStatus.Assigned || task.status == TaskStatus.InProgress -> {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { onAddNote(task.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                    ) {
                        Text(
                            text = if (task.notes.isBlank()) "Add Note / Complete" else "Edit Note / Complete",
                            color = AppColors.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskCard_Open() {
    TaskCard(
        task = TaskUi(
            id = "1",
            issueTitle = "Brake pad replacement",
            vehicleName = "Toyota Corolla – HSD343",
            time = "09:00 AM",
            category = "Maintenance",
            status = TaskStatus.Open,
            actionText = "Take Task",
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewTaskCard_Assigned() {
    TaskCard(
        task = TaskUi(
            id = "2",
            issueTitle = "Oil leak inspection",
            vehicleName = "Scania Railer – QTR552",
            time = "01:00 PM",
            category = "Inspection",
            status = TaskStatus.Assigned,
            notes = "Seep around rear main seal.",
        ),
    )
}
