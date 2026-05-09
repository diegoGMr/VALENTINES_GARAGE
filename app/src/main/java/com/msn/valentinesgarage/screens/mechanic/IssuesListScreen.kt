package com.msn.valentinesgarage.screens.mechanic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.data.models.Issue
import com.msn.valentinesgarage.screens.mechanic.viewmodels.IssuesViewModel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Truck

private val issueStatusFilters = listOf("All", "Pending", "Resolved")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesListScreen(
    token: String = "",
    isReadOnly: Boolean = false,
    onCreateIssue: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: IssuesViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadIssues(token)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreateIssueDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { visitId, desc ->
                viewModel.createIssue(token, visitId, desc)
                showCreateDialog = false
            }
        )
    }

    val filteredIssues = uiState.issues.filter { issue ->
        val matchesSearch = searchQuery.isBlank() ||
                issue.description.contains(searchQuery, ignoreCase = true)
        val statusText = if (issue.resolved == true) "Resolved" else "Pending"
        val matchesStatus = selectedStatus == "All" || statusText == selectedStatus
        matchesSearch && matchesStatus
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadIssues(token) },
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.OrangeWhite)
                .statusBarsPadding(),
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Issues",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.FontBlackStrong,
                    )
                }
                // Create issue button
                if (!isReadOnly) {
                    Box(
                        modifier = Modifier
                            .background(AppColors.Orange, RoundedCornerShape(10.dp))
                            .clickable(onClick = { showCreateDialog = true })
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Plus,
                                contentDescription = null,
                                tint = AppColors.White,
                                modifier = Modifier.size(10.dp),
                            )
                            Text(
                                text = "Report",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.White,
                            )
                        }
                    }
                }
            }

            // ... (rest of search bar and filters)

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Search,
                            contentDescription = null,
                            tint = AppColors.TextHint,
                            modifier = Modifier.size(14.dp),
                        )
                        Text("Search issues...", color = AppColors.TextHint, fontSize = 14.sp)
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

            Spacer(modifier = Modifier.height(10.dp))

            // Status filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(issueStatusFilters) { status ->
                    val isSelected = selectedStatus == status
                    Box(
                        modifier = Modifier
                            .border(
                                1.5.dp,
                                if (isSelected) AppColors.Orange else AppColors.LightGray,
                                RoundedCornerShape(8.dp),
                            )
                            .background(
                                if (isSelected) AppColors.Orange else AppColors.White,
                                RoundedCornerShape(8.dp),
                            )
                            .clickable { selectedStatus = status }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                    ) {
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) AppColors.White else AppColors.FontBlackMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredIssues.isEmpty() && !uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No issues found",
                        fontSize = 14.sp,
                        color = AppColors.TextHint,
                    )
                }
            } else {
                val groupedIssues = filteredIssues.groupBy { 
                    it.visit?.trucks?.plate_number ?: "Unknown Vehicle" 
                }

                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    groupedIssues.forEach { (vehicle, issues) ->
                        item(key = "vehicle_header_$vehicle") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.Truck,
                                    contentDescription = null,
                                    tint = AppColors.FontBlackMedium,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Vehicle: $vehicle",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.FontBlackMedium
                                )
                            }
                        }
                        
                        items(issues, key = { it.id }) { issue ->
                            IssueCard(
                                issue = issue,
                                isReadOnly = isReadOnly,
                                onResolve = {
                                    viewModel.resolveIssue(token, issue.id)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateIssueDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit,
) {
    var visitId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report New Issue") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = visitId,
                    onValueChange = { visitId = it },
                    label = { Text("Visit ID") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    val vId = visitId.toIntOrNull()
                    if (vId != null && description.isNotBlank()) {
                        onSubmit(vId, description)
                    }
                }
            ) { Text("Submit") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun IssueCard(
    issue: Issue,
    isReadOnly: Boolean,
    onResolve: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = "Issue #${issue.id}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.FontBlackStrong,
                    modifier = Modifier.weight(1f),
                )
                
                val statusText = if (issue.resolved == true) "Resolved" else "Pending"
                val statusColor = if (issue.resolved == true) AppColors.Green else AppColors.Orange

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = statusText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                    )
                }
            }

            Text(
                text = issue.description,
                fontSize = 13.sp,
                color = AppColors.FontBlackSoft,
                maxLines = 2,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Truck,
                            contentDescription = null,
                            tint = AppColors.TextHint,
                            modifier = Modifier.size(10.dp),
                        )
                        val plate = issue.visit?.trucks?.plate_number ?: "Visit #${issue.visitId}"
                        Text(text = plate, fontSize = 12.sp, color = AppColors.TextHint)
                    }
                    Text(text = "•", fontSize = 12.sp, color = AppColors.TextHint)
                    Text(text = "Mech #${issue.mechanicId}", fontSize = 12.sp, color = AppColors.TextHint)
                }

                if (!isReadOnly && issue.resolved != true) {
                    TextButton(
                        onClick = onResolve,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("Resolve", fontSize = 12.sp, color = AppColors.Orange)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun IssuesListScreenPreview() {
    IssuesListScreen()
}
