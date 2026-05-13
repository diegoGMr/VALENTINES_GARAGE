package com.msn.valentinesgarage.screens.mechanic

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
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.material3.TextButton
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
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.screens.mechanic.viewmodels.MechanicServiceHistoryViewModel
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.ChevronLeft
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.History

@Composable
fun MechanicServiceHistoryScreen(
    token: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: MechanicServiceHistoryViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(token) {
        viewModel.loadHistory(token)
    }

    val plateFilters = listOf("All") + uiState.visits
        .mapNotNull { it.truck?.plate_number }
        .distinct()
        .sorted()

    val filteredVisits = uiState.visits.filter { visit ->
        val selected = uiState.selectedPlate
        selected.isNullOrBlank() || selected == "All" || visit.truck?.plate_number == selected
    }

    ConfigureSystemBars(statusBarColor = AppColors.White)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.White,
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadHistory(token) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppColors.White)
                .topSafeDrawingPadding(),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item(key = "header") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
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
                                imageVector = FontAwesomeIcons.Solid.History,
                                contentDescription = null,
                                tint = AppColors.Orange,
                                modifier = Modifier.size(20.dp),
                            )
                            Column {
                                Text(
                                    text = "Service History",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.FontBlackStrong,
                                )
                                Text(
                                    text = "Track your visits, issues and resolutions",
                                    fontSize = 12.sp,
                                    color = AppColors.TextHint,
                                )
                            }
                        }
                    }
                }

                item(key = "filters") {
                    Text(
                        text = "Filter by vehicle plate",
                        fontSize = 12.sp,
                        color = AppColors.FontBlackMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(plateFilters) { plate ->
                            val selected = (uiState.selectedPlate ?: "All") == plate
                            TextButton(
                                onClick = { viewModel.selectPlate(if (plate == "All") null else plate) },
                                shape = RoundedCornerShape(10.dp),
                                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                    containerColor = if (selected) AppColors.OrangeWhite else AppColors.White,
                                    contentColor = if (selected) AppColors.Orange else AppColors.FontBlackMedium,
                                ),
                                modifier = Modifier
                                    .background(
                                        color = if (selected) AppColors.OrangeWhite else AppColors.White,
                                        shape = RoundedCornerShape(10.dp),
                                    )
                            ) {
                                Text(plate)
                            }
                        }
                    }
                }

                if (uiState.error != null) {
                    item(key = "error") {
                        Text(
                            text = uiState.error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                if (filteredVisits.isEmpty() && !uiState.isLoading) {
                    item(key = "empty") {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppColors.White),
                        ) {
                            Text(
                                text = "No service history found for selected filter.",
                                color = AppColors.TextHint,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                    }
                }

                items(filteredVisits, key = { it.visitId }) { visit ->
                    ServiceVisitHistoryCard(visit = visit)
                }
            }
        }
    }
}

@Composable
private fun ServiceVisitHistoryCard(visit: MechanicVisit) {
    val totalIssues = visit.issues.size
    val resolvedCount = visit.issues.count { it.resolved == true }
    val pendingCount = totalIssues - resolvedCount
    val totalCost = visit.issues.sumOf { it.cost ?: 0.0 }
    val plate = visit.truck?.plate_number ?: "Unknown Plate"
    val specialty = visit.truck?.speciality?.name ?: ""
    val completed = !visit.completedInfo.isNullOrEmpty()

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Car,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(14.dp),
                    )
                    Text(
                        text = if (specialty.isNotBlank()) "$plate ($specialty)" else plate,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.FontBlackStrong,
                        fontSize = 14.sp,
                    )
                }

                Text(
                    text = if (completed) "Completed" else "Active",
                    fontSize = 11.sp,
                    color = if (completed) AppColors.Green else AppColors.Orange,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Text(
                text = "Visit #${visit.visitId} • Client: ${visit.client?.full_name ?: "Unknown"}",
                fontSize = 12.sp,
                color = AppColors.FontBlackSoft,
            )

            if (!visit.clientNotes.isNullOrBlank()) {
                Text(
                    text = "Notes: ${visit.clientNotes}",
                    fontSize = 12.sp,
                    color = AppColors.TextHint,
                )
            }

            HorizontalDivider(color = AppColors.LightGray.copy(alpha = 0.5f))

            Text(
                text = "Issues: $resolvedCount/$totalIssues resolved • $pendingCount pending • Cost R${"%.2f".format(totalCost)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (pendingCount == 0 && totalIssues > 0) AppColors.Green else AppColors.Orange,
            )

            if (visit.issues.isEmpty()) {
                Text(
                    text = "No issues recorded on this visit.",
                    fontSize = 12.sp,
                    color = AppColors.TextHint,
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    visit.issues.forEach { issue ->
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
                                        imageVector = if (issue.resolved == true) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.ExclamationTriangle,
                                        contentDescription = null,
                                        tint = if (issue.resolved == true) AppColors.Green else AppColors.Orange,
                                        modifier = Modifier.size(11.dp),
                                    )
                                    Text(
                                        text = "Issue #${issue.id}: ${issue.description}",
                                        fontSize = 12.sp,
                                        color = AppColors.FontBlackMedium,
                                        fontWeight = FontWeight.Medium,
                                    )
                                }

                                Text(
                                    text = "Resolution: ${issue.resolutionNotes?.takeIf { it.isNotBlank() } ?: "No resolution notes"}",
                                    fontSize = 11.sp,
                                    color = if (issue.resolved == true) AppColors.Green else AppColors.TextHint,
                                )

                                if (issue.cost != null) {
                                    Text(
                                        text = "Cost: R${"%.2f".format(issue.cost)}",
                                        fontSize = 11.sp,
                                        color = AppColors.FontBlackSoft,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}


