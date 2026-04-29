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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Plus
import compose.icons.fontawesomeicons.solid.Search
import compose.icons.fontawesomeicons.solid.Truck

// UI model for a single issue in the list
data class IssueUi(
    val id: String,
    val title: String,
    val description: String,
    val vehiclePlate: String,
    val category: String,
    val severity: IssueSeverity,
    val status: IssueStatus,
    val reportedBy: String,
    val reportedAt: String,
)

enum class IssueSeverity(val label: String, val color: Color) {
    Low("Low", AppColors.Green),
    Medium("Medium", AppColors.Orange),
    High("High", AppColors.Pink),
    Critical("Critical", AppColors.Red),
}

enum class IssueStatus(val label: String, val color: Color) {
    Open("Open", AppColors.Orange),
    InProgress("In Progress", AppColors.Green),
    PendingParts("Pending Parts", AppColors.Pink),
    Resolved("Resolved", AppColors.FontBlackSoft),
}

private val issueStatusFilters = listOf("All") + IssueStatus.entries.map { it.label }
private val issueCategoryFilters = listOf("All", "Engine", "Brakes", "Electrical", "Transmission", "Tyres", "Suspension")

@Composable
fun IssuesListScreen(
    isReadOnly: Boolean = false,
    onCreateIssue: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // TODO: Backend - GET /issues/getMechanicIssues/{mechanicId} to load relevant issues for this mechanic
    // TODO: Backend - for inspectors: GET /issues/getAll (read-only access, isReadOnly = true)
    // TODO: Backend - PUT /issues/updateStatus/{issueId} to update issue status (not for inspectors)
    // TODO: Backend - implement search via GET /issues/search?query={searchQuery}&category={cat}&status={status}

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf("All") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Sample issues — replace with API response
    val allIssues = listOf(
        IssueUi("i1", "Engine oil leak", "Oil seeping from rear main seal", "HSD343", "Engine", IssueSeverity.High, IssueStatus.Open, "Simon Rivers", "28 Apr 2026"),
        IssueUi("i2", "Brake pad worn", "Front left pad below minimum thickness", "JKL918", "Brakes", IssueSeverity.Critical, IssueStatus.InProgress, "Joseph Oceanside", "27 Apr 2026"),
        IssueUi("i3", "Electrical fault", "Dashboard warning lights intermittently on", "QTR552", "Electrical", IssueSeverity.Medium, IssueStatus.Open, "Robert Mountain", "26 Apr 2026"),
        IssueUi("i4", "Tyre pressure low", "Left rear tyre dropping pressure", "HSD343", "Tyres", IssueSeverity.Low, IssueStatus.PendingParts, "Simon Rivers", "25 Apr 2026"),
        IssueUi("i5", "Coolant leak", "Small leak at radiator hose connection", "BRT001", "Cooling", IssueSeverity.Medium, IssueStatus.Resolved, "Jonas Desert", "22 Apr 2026"),
    )

    val filteredIssues = allIssues.filter { issue ->
        val matchesSearch = searchQuery.isBlank() ||
                issue.title.contains(searchQuery, ignoreCase = true) ||
                issue.vehiclePlate.contains(searchQuery, ignoreCase = true) ||
                issue.description.contains(searchQuery, ignoreCase = true)
        val matchesStatus = selectedStatus == "All" || issue.status.label == selectedStatus
        val matchesCategory = selectedCategory == "All" || issue.category == selectedCategory
        matchesSearch && matchesStatus && matchesCategory
    }

    Column(
        modifier = modifier
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
            // Create issue button — hidden for inspectors
            if (!isReadOnly) {
                Box(
                    modifier = Modifier
                        .background(AppColors.Orange, RoundedCornerShape(10.dp))
                        .clickable(onClick = onCreateIssue)
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

        Spacer(modifier = Modifier.height(8.dp))

        // Category filter
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(issueCategoryFilters) { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (isSelected) AppColors.NearBlack else AppColors.LightGray,
                            RoundedCornerShape(6.dp),
                        )
                        .background(
                            if (isSelected) AppColors.NearBlack else AppColors.White,
                            RoundedCornerShape(6.dp),
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = category,
                        fontSize = 11.sp,
                        color = if (isSelected) AppColors.White else AppColors.FontBlackMedium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredIssues.isEmpty()) {
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
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(filteredIssues, key = { it.id }) { issue ->
                    IssueCard(
                        issue = issue,
                        isReadOnly = isReadOnly,
                        onStatusUpdate = { newStatus ->
                            // TODO: Backend - PUT /issues/updateStatus/{issueId} with { status: newStatus }
                            // On success: refresh issue list
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun IssueCard(
    issue: IssueUi,
    isReadOnly: Boolean,
    onStatusUpdate: (String) -> Unit,
) {
    var statusExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
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
                    text = issue.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.FontBlackStrong,
                    modifier = Modifier.weight(1f),
                )
                // Severity badge
                Box(
                    modifier = Modifier
                        .background(issue.severity.color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text(
                        text = issue.severity.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = issue.severity.color,
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
                    Text(text = issue.vehiclePlate, fontSize = 12.sp, color = AppColors.TextHint)
                }
                Text(text = "•", fontSize = 12.sp, color = AppColors.TextHint)
                Text(text = issue.category, fontSize = 12.sp, color = AppColors.TextHint)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Clock,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(text = issue.reportedAt, fontSize = 11.sp, color = AppColors.TextHint)
                }

                Box {
                    // Status badge — tappable if not read-only
                    Box(
                        modifier = Modifier
                            .background(issue.status.color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .then(
                                if (!isReadOnly) Modifier.clickable { statusExpanded = true }
                                else Modifier
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                    ) {
                        Text(
                            text = if (!isReadOnly) "${issue.status.label} ▾" else issue.status.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = issue.status.color,
                        )
                    }
                    if (!isReadOnly) {
                        DropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false },
                        ) {
                            IssueStatus.entries.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status.label) },
                                    onClick = {
                                        onStatusUpdate(status.label)
                                        statusExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Reported by ${issue.reportedBy}",
                fontSize = 11.sp,
                color = AppColors.TextHint,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun IssuesListScreenPreview() {
    IssuesListScreen()
}
