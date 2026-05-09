package com.msn.valentinesgarage.screens.mechanic

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.screens.mechanic.viewmodels.MechanicVehiclesViewModel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Tools

@Composable
fun MechanicVehiclesScreen(
    token: String,
    modifier: Modifier = Modifier,
    viewModel: MechanicVehiclesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedVisitForIssue by remember { mutableStateOf<MechanicVisit?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadAssignedVehicles(token)
    }

    if (selectedVisitForIssue != null) {
        ReportIssueDialog(
            visit = selectedVisitForIssue!!,
            onDismiss = { selectedVisitForIssue = null },
            onReport = { description ->
                viewModel.reportIssue(token, selectedVisitForIssue!!.visitId, description)
                selectedVisitForIssue = null
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadAssignedVehicles(token) },
            modifier = modifier
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
                            "Assigned Vehicles",
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
                            Text("No vehicles currently assigned to you.", color = AppColors.TextHint)
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

            Spacer(Modifier.height(16.dp))
            
            Button(
                onClick = onCompleteVisit,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Green)
            ) {
                Icon(FontAwesomeIcons.Solid.Check, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Complete Service", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun ReportIssueDialog(
    visit: MechanicVisit,
    onDismiss: () -> Unit,
    onReport: (String) -> Unit
) {
    var description by remember { mutableStateOf("") }

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
            }
        },
        confirmButton = {
            Button(
                onClick = { if (description.isNotBlank()) onReport(description) },
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
