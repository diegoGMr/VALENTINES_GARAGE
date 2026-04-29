package com.msn.valentinesgarage.screens.mechanic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Calendar
import compose.icons.fontawesomeicons.solid.ChevronDown
import compose.icons.fontawesomeicons.solid.Tasks
import compose.icons.fontawesomeicons.solid.User

// This screen is restricted to lead_mechanic role only.
// TODO: Backend - add role guard: only render this screen if userProfile.role == "lead_mechanic"

private val taskCategories = listOf("Maintenance", "Inspection", "Repair", "Parts Replacement", "Diagnostics", "Other")
private val taskPriorities = listOf("Low", "Medium", "High", "Urgent")

@Composable
fun TaskCreationScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: Backend - POST /tasks/create with { description, assignedMechanicId, priority, category, linkedIssueId, dueDate }
    // TODO: Backend - GET /mechanics/getAll to load mechanic list for assignment dropdown
    // TODO: Backend - GET /issues/getOpen to load open issues for linking
    // TODO: Backend - on success: show confirmation and navigate back to tasks list

    var taskDescription by remember { mutableStateOf("") }
    var selectedMechanic by remember { mutableStateOf<String?>(null) }
    var selectedPriority by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedIssue by remember { mutableStateOf<String?>(null) }
    var dueDate by remember { mutableStateOf("") }
    var mechanicExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var issueExpanded by remember { mutableStateOf(false) }
    var submissionSuccess by remember { mutableStateOf(false) }

    // Sample data — replace with API responses
    val mechanics = listOf("Robert Mountain", "Simon Rivers", "Joseph Oceanside", "Jonas Desert")
    val openIssues = listOf("ISS-001: Engine oil leak – HSD343", "ISS-002: Brake wear – JKL918", "ISS-003: Electrical fault – QTR552")

    if (submissionSuccess) {
        TaskSubmissionSuccess(onCreateAnother = { submissionSuccess = false })
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Tasks,
                    contentDescription = null,
                    tint = AppColors.Orange,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Create Task",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.FontBlackStrong,
                )
            }
            // Role notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(AppColors.OrangeWhite, RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "Lead Mechanic only — assign tasks to your team members",
                    fontSize = 12.sp,
                    color = AppColors.Orange,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        item(key = "description") {
            SectionLabel(
                text = "Task Description",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = taskDescription,
                onValueChange = { taskDescription = it },
                placeholder = {
                    Text("Describe what needs to be done...", color = AppColors.TextHint, fontSize = 14.sp)
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Orange,
                    unfocusedBorderColor = AppColors.LightGray,
                    focusedTextColor = AppColors.FontBlackMedium,
                    unfocusedTextColor = AppColors.FontBlackMedium,
                    focusedContainerColor = AppColors.White,
                    unfocusedContainerColor = AppColors.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }

        item(key = "assign_mechanic") {
            SectionLabel(
                text = "Assign To",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            // TODO: Backend - populate from GET /mechanics/getAll; display name + role for each
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppColors.LightGray, RoundedCornerShape(12.dp))
                        .clickable { mechanicExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.User,
                            contentDescription = null,
                            tint = if (selectedMechanic != null) AppColors.Orange else AppColors.TextHint,
                            modifier = Modifier.size(14.dp),
                        )
                        Text(
                            text = selectedMechanic ?: "Select mechanic",
                            fontSize = 14.sp,
                            color = if (selectedMechanic == null) AppColors.TextHint else AppColors.FontBlackMedium,
                        )
                    }
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ChevronDown,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(12.dp),
                    )
                }
                DropdownMenu(
                    expanded = mechanicExpanded,
                    onDismissRequest = { mechanicExpanded = false },
                ) {
                    mechanics.forEach { name ->
                        DropdownMenuItem(
                            text = { Text(name) },
                            onClick = {
                                selectedMechanic = name
                                mechanicExpanded = false
                            },
                        )
                    }
                }
            }
        }

        item(key = "priority") {
            SectionLabel(
                text = "Priority",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(taskPriorities) { priority ->
                    val isSelected = selectedPriority == priority
                    val chipColor = when (priority) {
                        "Urgent" -> AppColors.Red
                        "High" -> AppColors.Pink
                        "Medium" -> AppColors.Orange
                        else -> AppColors.Green
                    }
                    Box(
                        modifier = Modifier
                            .border(
                                1.5.dp,
                                if (isSelected) chipColor else AppColors.LightGray,
                                RoundedCornerShape(8.dp),
                            )
                            .background(
                                if (isSelected) chipColor.copy(alpha = 0.12f) else AppColors.White,
                                RoundedCornerShape(8.dp),
                            )
                            .clickable { selectedPriority = priority }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = priority,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) chipColor else AppColors.FontBlackMedium,
                        )
                    }
                }
            }
        }

        item(key = "category") {
            SectionLabel(
                text = "Category",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppColors.LightGray, RoundedCornerShape(12.dp))
                        .clickable { categoryExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = selectedCategory ?: "Select category",
                        fontSize = 14.sp,
                        color = if (selectedCategory == null) AppColors.TextHint else AppColors.FontBlackMedium,
                    )
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ChevronDown,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(12.dp),
                    )
                }
                DropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false },
                ) {
                    taskCategories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                selectedCategory = cat
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }
        }

        item(key = "link_issue") {
            SectionLabel(
                text = "Link to Issue (optional)",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            // TODO: Backend - populate from GET /issues/getOpen?mechanicId={mechanicId}
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppColors.LightGray, RoundedCornerShape(12.dp))
                        .clickable { issueExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = selectedIssue ?: "Select issue (optional)",
                        fontSize = 14.sp,
                        color = if (selectedIssue == null) AppColors.TextHint else AppColors.FontBlackMedium,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                    )
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ChevronDown,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(12.dp),
                    )
                }
                DropdownMenu(
                    expanded = issueExpanded,
                    onDismissRequest = { issueExpanded = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("None", color = AppColors.TextHint) },
                        onClick = {
                            selectedIssue = null
                            issueExpanded = false
                        },
                    )
                    openIssues.forEach { issue ->
                        DropdownMenuItem(
                            text = { Text(issue) },
                            onClick = {
                                selectedIssue = issue
                                issueExpanded = false
                            },
                        )
                    }
                }
            }
        }

        item(key = "due_date") {
            SectionLabel(
                text = "Due Date",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            // TODO: Backend - replace OutlinedTextField with DatePickerDialog for native date selection
            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                placeholder = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Calendar,
                            contentDescription = null,
                            tint = AppColors.TextHint,
                            modifier = Modifier.size(14.dp),
                        )
                        Text("DD/MM/YYYY", color = AppColors.TextHint, fontSize = 14.sp)
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Orange,
                    unfocusedBorderColor = AppColors.LightGray,
                    focusedTextColor = AppColors.FontBlackMedium,
                    unfocusedTextColor = AppColors.FontBlackMedium,
                    focusedContainerColor = AppColors.White,
                    unfocusedContainerColor = AppColors.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            )
        }

        item(key = "submit") {
            Spacer(modifier = Modifier.height(4.dp))
            val isValid = taskDescription.isNotBlank() && selectedMechanic != null &&
                    selectedPriority != null && selectedCategory != null && dueDate.isNotBlank()
            Button(
                onClick = {
                    // TODO: Backend - POST /tasks/create with:
                    //   { description: taskDescription, assignedMechanicId, priority: selectedPriority,
                    //     category: selectedCategory, linkedIssueId: selectedIssue, dueDate }
                    // On success: submissionSuccess = true; On error: show error dialog
                    if (isValid) submissionSuccess = true
                },
                enabled = isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Orange,
                    contentColor = AppColors.White,
                    disabledContainerColor = AppColors.LightGray,
                    disabledContentColor = AppColors.TextHint,
                ),
            ) {
                Text(
                    text = "Create Task",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun TaskSubmissionSuccess(onCreateAnother: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.White)
            .statusBarsPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(AppColors.Orange, RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Tasks,
                contentDescription = null,
                tint = AppColors.White,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Task Created!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.FontBlackStrong,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "The task has been assigned to the mechanic and is now visible in their task list.",
            fontSize = 14.sp,
            color = AppColors.TextHint,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onCreateAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Orange,
                contentColor = AppColors.White,
            ),
        ) {
            Text("Create Another Task", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun TaskCreationScreenPreview() {
    TaskCreationScreen()
}
