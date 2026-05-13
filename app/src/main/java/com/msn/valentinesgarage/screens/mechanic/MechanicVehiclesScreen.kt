package com.msn.valentinesgarage.screens.mechanic

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.screens.mechanic.viewmodels.MechanicVehiclesViewModel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Tools
import kotlinx.coroutines.delay

@Composable
fun MechanicVehiclesScreen(
    token: String,
    modifier: Modifier = Modifier,
    viewModel: MechanicVehiclesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedVisitForIssue by remember { mutableStateOf<MechanicVisit?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showCompletionOverlay by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAssignedVehicles(token)
    }

    LaunchedEffect(uiState.visitCompleted) {
        if (uiState.visitCompleted) {
            showCompletionOverlay = true
        }
    }

    LaunchedEffect(showCompletionOverlay) {
        if (showCompletionOverlay) {
            delay(2200)
            showCompletionOverlay = false
        }
    }

    if (selectedVisitForIssue != null) {
        ReportIssueDialog(
            visit = selectedVisitForIssue!!,
            onDismiss = { selectedVisitForIssue = null },
            onReport = { description, cost ->
                viewModel.reportIssue(token, selectedVisitForIssue!!.visitId, description, cost)
                selectedVisitForIssue = null
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            PullToRefreshBox(
                isRefreshing = uiState.isLoading,
                onRefresh = { viewModel.loadAssignedVehicles(token) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(AppColors.White)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                FontAwesomeIcons.Solid.Tools,
                                contentDescription = null,
                                tint = AppColors.Orange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Active Vehicles",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.FontBlackStrong
                            )
                        }
                    }

                    if (uiState.visits.isEmpty() && !uiState.isLoading) {
                        item {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No active vehicles in service.", color = AppColors.TextHint)
                            }
                        }
                    }

                    items(uiState.visits) { visit ->
                        MechanicVehicleCard(
                            visit = visit,
                            onReportIssue = { selectedVisitForIssue = visit },
                            onCompleteVisit = { viewModel.completeVisit(token, visit.visitId) }
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showCompletionOverlay,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(
                initialScale = 0.6f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
            exit = fadeOut(animationSpec = tween(400)) + scaleOut(targetScale = 0.8f)
        ) {
            ServiceCompletionOverlay()
        }
    }
}

@Composable
private fun ServiceCompletionOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .background(AppColors.Green, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    FontAwesomeIcons.Solid.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Service Completed!",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "The vehicle has been marked as done.",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MechanicVehicleCard(
    visit: MechanicVisit,
    onReportIssue: () -> Unit,
    onCompleteVisit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
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
                        val plate = visit.truck?.plate_number ?: "Unknown Plate"
                        val specialty = visit.truck?.speciality?.name ?: ""
                        Text(
                            if (specialty.isNotEmpty()) "$plate ($specialty)" else plate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            "Client: ${visit.client?.full_name ?: "Unknown"}",
                            fontSize = 13.sp,
                            color = AppColors.FontBlackMedium
                        )
                    }
                }

                Button(
                    onClick = onReportIssue,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.OrangeWhite, contentColor = AppColors.Orange),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(FontAwesomeIcons.Solid.ExclamationTriangle, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Report", fontSize = 12.sp)
                }
            }

            if (!visit.clientNotes.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        "Client Notes: ${visit.clientNotes}",
                        fontSize = 12.sp,
                        color = AppColors.FontBlackMedium
                    )
                }
            }

            if (visit.issues.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                val pendingCount = visit.issues.count { it.resolved != true }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (pendingCount == 0) FontAwesomeIcons.Solid.Check else FontAwesomeIcons.Solid.ExclamationTriangle,
                        null,
                        tint = if (pendingCount == 0) AppColors.Green else AppColors.Orange,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (pendingCount == 0) "All issues resolved" else "$pendingCount pending issue(s)",
                        fontSize = 12.sp,
                        color = if (pendingCount == 0) AppColors.Green else AppColors.Orange,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            val allResolved = visit.issues.all { it.resolved == true }
            Button(
                onClick = onCompleteVisit,
                enabled = allResolved,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Green,
                    disabledContainerColor = AppColors.LightGray
                )
            ) {
                Icon(FontAwesomeIcons.Solid.Check, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    if (allResolved) "Complete Service" else "Resolve Issues to Complete",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun ReportIssueDialog(
    visit: MechanicVisit,
    onDismiss: () -> Unit,
    onReport: (description: String, cost: Double?) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Issue for ${visit.truck?.plate_number}") },
        text = {
            Column {
                Text("Describe the issue found on this vehicle:", fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. Brake pads worn out") },
                    minLines = 3
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = costText,
                    onValueChange = { costText = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Estimated Cost (Optional)") },
                    placeholder = { Text("e.g. 350.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotBlank())
                        onReport(description, costText.toDoubleOrNull())
                },
                enabled = description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
