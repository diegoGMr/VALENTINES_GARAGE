package com.msn.valentinesgarage.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.data.models.AdminMechanicHistoryIssue
import com.msn.valentinesgarage.data.models.AdminMechanicHistoryVisit
import com.msn.valentinesgarage.screens.admin.viewmodels.AdminMechanicHistoryViewModel
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.ChevronLeft
import compose.icons.fontawesomeicons.solid.ClipboardList
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Tools

@Composable
fun AdminMechanicHistoryScreen(
    token: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: AdminMechanicHistoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        viewModel.loadHistory(token)
    }

    ConfigureSystemBars(statusBarColor = AppColors.OrangeWhite)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.OrangeWhite,
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadHistory(token) },
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.OrangeWhite)
                .padding(innerPadding)
                .topSafeDrawingPadding(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "header") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            IconButton(onClick = onBack) {
                                Icon(
                                    imageVector = FontAwesomeIcons.Solid.ChevronLeft,
                                    contentDescription = "Back",
                                    tint = AppColors.FontBlackStrong,
                                )
                            }
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.ClipboardList,
                                contentDescription = null,
                                tint = AppColors.Orange,
                                modifier = Modifier.size(20.dp),
                            )
                            Column {
                                Text(
                                    text = "Mechanic Full History",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.FontBlackStrong,
                                )
                                Text(
                                    text = "Grouped by visit service flow",
                                    fontSize = 12.sp,
                                    color = AppColors.TextHint,
                                )
                            }
                        }
                    }
                }

                if (uiState.error != null) {
                    item(key = "error") {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }

                if (uiState.visits.isEmpty() && !uiState.isLoading) {
                    item(key = "empty") {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.White),
                        ) {
                            Text(
                                text = "No visit history found yet.",
                                color = AppColors.TextHint,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                    }
                }

                items(uiState.visits, key = { it.visitId }) { visit ->
                    VisitHistoryCard(visit = visit)
                }
            }
        }
    }
}

@Composable
private fun VisitHistoryCard(visit: AdminMechanicHistoryVisit) {
    val summary = visit.summary

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "SERVICE / VISIT #${visit.visitId}",
                    color = AppColors.Orange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = if (summary?.isCompleted == true) "Completed" else "Active",
                    fontSize = 11.sp,
                    color = if (summary?.isCompleted == true) AppColors.Green else AppColors.Orange,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            InfoRow(label = "Booking", value = visit.booking?.bookingId?.toString() ?: "Not linked")
            InfoRow(
                label = "Booked at",
                value = listOfNotNull(visit.booking?.bookingDate, visit.booking?.bookingTime).joinToString(" ").ifBlank { "N/A" },
            )
            InfoRow(label = "Vehicle", value = visit.truck?.licensePlate ?: "Unknown vehicle")
            InfoRow(
                label = "Truck Type",
                value = visit.truck?.speciality ?: "N/A",
            )
            InfoRow(
                label = "Client",
                value = visit.client?.fullName ?: "Unknown client",
            )

            val bookingNotes = visit.booking?.clientNotes?.takeIf { it.isNotBlank() } ?: "No booking notes"
            InfoRow(label = "Booking Notes", value = bookingNotes)

            val serviceNotes = visit.serviceNotes?.takeIf { it.isNotBlank() } ?: "No service notes"
            InfoRow(label = "Service Notes", value = serviceNotes)

            HorizontalDivider(color = AppColors.LightGray.copy(alpha = 0.5f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Tools,
                    contentDescription = null,
                    tint = AppColors.FontBlackSoft,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = "Mechanics on this visit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.FontBlackMedium,
                )
            }

            if (visit.mechanics.isEmpty()) {
                Text(text = "No mechanic assignment recorded", color = AppColors.TextHint, fontSize = 12.sp)
            } else {
                visit.mechanics.forEach { mechanic ->
                    Text(
                        text = "- ${mechanic.fullName} (${mechanic.role})",
                        fontSize = 12.sp,
                        color = AppColors.FontBlackSoft,
                    )
                }
            }

            HorizontalDivider(color = AppColors.LightGray.copy(alpha = 0.5f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.ClipboardList,
                    contentDescription = null,
                    tint = AppColors.FontBlackSoft,
                    modifier = Modifier.size(12.dp),
                )
                Text(
                    text = "Issues and Resolutions",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.FontBlackMedium,
                )
            }

            if (visit.issues.isEmpty()) {
                Text(text = "No issues recorded for this visit", color = AppColors.TextHint, fontSize = 12.sp)
            } else {
                visit.issues.forEach { issue ->
                    IssueHistoryCard(issue = issue)
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Summary: ${summary?.resolvedIssues ?: 0}/${summary?.totalIssues ?: 0} resolved • Total Cost R${"%.2f".format(summary?.totalCost ?: 0.0)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.FontBlackMedium,
            )
        }
    }
}

@Composable
private fun IssueHistoryCard(issue: AdminMechanicHistoryIssue) {
    val isResolved = issue.issueResolved
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.LightGray.copy(alpha = 0.18f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                    contentDescription = null,
                    tint = if (isResolved) AppColors.Green else AppColors.Orange,
                    modifier = Modifier.size(11.dp),
                )
                Text(
                    text = "Issue #${issue.issueId}: ${issue.issueDescription}",
                    fontSize = 12.sp,
                    color = AppColors.FontBlackMedium,
                    fontWeight = FontWeight.Medium,
                )
            }

            Text(
                text = "Raised by: ${issue.mechanic?.fullName ?: "Unknown mechanic"}",
                fontSize = 11.sp,
                color = AppColors.FontBlackSoft,
            )
            Text(
                text = "Status: ${if (isResolved) "Resolved" else "Pending"}",
                fontSize = 11.sp,
                color = if (isResolved) AppColors.Green else AppColors.Orange,
            )
            Text(
                text = "Resolution: ${issue.issueResolutionNotes?.takeIf { it.isNotBlank() } ?: "No resolution notes"}",
                fontSize = 11.sp,
                color = AppColors.FontBlackSoft,
            )
            Text(
                text = "Cost: R${"%.2f".format(issue.cost ?: 0.0)}",
                fontSize = 11.sp,
                color = AppColors.FontBlackSoft,
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = "$label:",
            fontSize = 12.sp,
            color = AppColors.TextHint,
            modifier = Modifier.weight(0.35f),
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = AppColors.FontBlackSoft,
            modifier = Modifier.weight(0.65f),
        )
    }
}


