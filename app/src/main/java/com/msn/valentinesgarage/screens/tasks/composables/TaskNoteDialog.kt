package com.msn.valentinesgarage.screens.tasks.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.theme.AppColors

@Composable
fun TaskNoteDialog(
    task: TaskUi,
    noteValue: String,
    onNoteChanged: (String) -> Unit,
    onSaveNote: () -> Unit,
    onCompleteTask: () -> Unit,
    onDismiss: () -> Unit,
) {
     Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppColors.White,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                )
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = task.issueTitle,
                color = AppColors.FontBlackStrong,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Vehicle: ${task.vehicleName}",
                color = AppColors.FontBlackSoft,
                fontSize = 13.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Mechanic Notes",
                color = AppColors.FontBlackMedium,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            OutlinedTextField(
                value = noteValue,
                onValueChange = onNoteChanged,
                placeholder = {
                    Text(
                        text = "Describe what you worked on, parts replaced, observations…",
                        color = AppColors.TextHint,
                        fontSize = 13.sp,
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                minLines = 4,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Orange,
                    unfocusedBorderColor = AppColors.LightGray,
                    focusedContainerColor = AppColors.White,
                    unfocusedContainerColor = AppColors.White,
                    cursorColor = AppColors.Orange,
                    focusedTextColor = AppColors.FontBlackMedium,
                    unfocusedTextColor = AppColors.FontBlackMedium,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = onCompleteTask,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(
                    text = "Mark as Completed",
                    color = AppColors.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Button(
                onClick = onSaveNote,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Text(
                    text = "Save Note Only",
                    color = AppColors.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Text(
                    text = "Cancel",
                    color = AppColors.FontBlackSoft,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun PreviewTaskNoteDialog() {
    TaskNoteDialog(
        task = TaskUi(
            id = "3",
            issueTitle = "Oil leak inspection",
            vehicleName = "Scania Railer – QTR552",
            time = "01:00 PM",
            category = "Inspection",
            status = TaskStatus.Assigned,
        ),
        noteValue = "Seep around rear main seal.",
        onNoteChanged = {},
        onSaveNote = {},
        onCompleteTask = {},
        onDismiss = {},
    )
}
