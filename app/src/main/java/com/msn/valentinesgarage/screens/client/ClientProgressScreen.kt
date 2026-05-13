package com.msn.valentinesgarage.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.screens.client.viewmodels.ClientProgressViewModel
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Tasks

@Composable
fun ClientProgressScreen(
    token: String,
    modifier: Modifier = Modifier,
    viewModel: ClientProgressViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ConfigureSystemBars(statusBarColor = AppColors.White)

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadProgress(token)
        }
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadProgress(token) },
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
            .topSafeDrawingPadding()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        FontAwesomeIcons.Solid.Tasks,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Vehicle Progress",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.FontBlackStrong
                    )
                }
            }

            if (uiState.visits.isEmpty() && !uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No active service records found.", color = AppColors.TextHint)
                    }
                }
            }

            items(uiState.visits) { visit ->
                ClientVehicleProgressCard(visit = visit)
            }
        }
    }
}

@Composable
fun ClientVehicleProgressCard(visit: MechanicVisit) {
    val plate = visit.truck?.plate_number ?: "Unknown Plate"
    val specialty = visit.truck?.speciality?.name ?: ""
    val isCompleted = !visit.completedInfo.isNullOrEmpty()
    val issues = visit.issues
    val totalIssues = issues.size
    val resolvedIssues = issues.count { it.resolved == true }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(AppColors.Orange.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(FontAwesomeIcons.Solid.Car, null, tint = AppColors.Orange, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            if (specialty.isNotEmpty()) "$plate ($specialty)" else plate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = AppColors.FontBlackStrong
                        )
                        Text(
                            "Visit ID: #${visit.visitId}",
                            fontSize = 12.sp,
                            color = AppColors.TextHint
                        )
                    }
                }

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .background(AppColors.Green.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "READY FOR PICK-UP",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AppColors.Green
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Service Progress", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))

            val progress = if (isCompleted) 1f else (if (totalIssues > 0) resolvedIssues.toFloat() / totalIssues else 0f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (isCompleted || progress == 1f) AppColors.Green else AppColors.Orange,
                trackColor = AppColors.LightGray.copy(alpha = 0.3f),
            )

            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    when {
                        isCompleted -> "Vehicle ready for pick-up"
                        progress == 1f -> "All issues resolved"
                        else -> "In Progress"
                    },
                    fontSize = 12.sp,
                    color = if (isCompleted || progress == 1f) AppColors.Green else AppColors.Orange,
                    fontWeight = FontWeight.Medium
                )
                if (!isCompleted) {
                    Text("$resolvedIssues/$totalIssues Issues", fontSize = 12.sp, color = AppColors.TextHint)
                }
            }

            if (issues.isNotEmpty() && !isCompleted) {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = AppColors.LightGray.copy(alpha = 0.5f))
                Spacer(Modifier.height(12.dp))
                Text("Reported Issues:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))

                issues.forEach { issue ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (issue.resolved == true) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.ExclamationTriangle,
                            contentDescription = null,
                            tint = if (issue.resolved == true) AppColors.Green else AppColors.Orange,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = issue.description,
                            fontSize = 12.sp,
                            color = AppColors.FontBlackMedium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
