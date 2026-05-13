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
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Camera
import compose.icons.fontawesomeicons.solid.ChevronDown
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Image
import compose.icons.fontawesomeicons.solid.Plus

private val issueCategories = listOf("Engine", "Brakes", "Electrical", "Transmission", "Tyres", "Suspension", "Cooling", "Other")
private val issueSeverities = listOf("Low", "Medium", "High", "Critical")

@Composable
fun IssueCreationScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: Backend - POST /issues/create with { description, severity, vehiclePlate, category, mechanicId, imageUrls[] }
    // TODO: Backend - GET /truck/getAllTrucks or /truck/getMechanicTrucks/{mechanicId} to populate vehicle dropdown
    // TODO: Backend - POST /issues/uploadAttachment/{issueId} via Supabase imageService for image uploads
    // TODO: Backend - on success: show confirmation dialog and navigate back to IssuesListScreen

    var description by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSeverity by remember { mutableStateOf<String?>(null) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var vehicleExpanded by remember { mutableStateOf(false) }
    var submissionSuccess by remember { mutableStateOf(false) }

    // Sample vehicle plates — replace with API response
    val availableVehicles = listOf("HSD343", "JKL918", "QTR552", "BRT001", "NMZ772")

    ConfigureSystemBars(statusBarColor = AppColors.White)

    if (submissionSuccess) {
        IssueSubmissionSuccess(onCreateAnother = { submissionSuccess = false })
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
            .topSafeDrawingPadding(),
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
                    imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                    contentDescription = null,
                    tint = AppColors.Orange,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = "Report an Issue",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.FontBlackStrong,
                )
            }
        }

        item(key = "description") {
            SectionLabel(
                text = "Issue Description",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text("Describe the issue in detail...", color = AppColors.TextHint, fontSize = 14.sp)
                },
                minLines = 4,
                maxLines = 6,
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

        item(key = "vehicle") {
            SectionLabel(
                text = "Vehicle Identification",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            // TODO: Backend - replace with real truck list from GET /truck/getMechanicTrucks/{mechanicId}
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, AppColors.LightGray, RoundedCornerShape(12.dp))
                        .clickable { vehicleExpanded = true }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = vehiclePlate.ifBlank { "Select vehicle plate" },
                        fontSize = 14.sp,
                        color = if (vehiclePlate.isBlank()) AppColors.TextHint else AppColors.FontBlackMedium,
                    )
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.ChevronDown,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(12.dp),
                    )
                }
                DropdownMenu(
                    expanded = vehicleExpanded,
                    onDismissRequest = { vehicleExpanded = false },
                ) {
                    availableVehicles.forEach { plate ->
                        DropdownMenuItem(
                            text = { Text(plate) },
                            onClick = {
                                vehiclePlate = plate
                                vehicleExpanded = false
                            },
                        )
                    }
                }
            }
        }

        item(key = "severity") {
            SectionLabel(
                text = "Severity",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(issueSeverities) { severity ->
                    val isSelected = selectedSeverity == severity
                    val chipColor = when (severity) {
                        "Critical" -> AppColors.Red
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
                            .clickable { selectedSeverity = severity }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = severity,
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
                    issueCategories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                categoryExpanded = false
                            },
                        )
                    }
                }
            }
        }

        item(key = "attachments") {
            SectionLabel(
                text = "Attachments",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(6.dp))
            // TODO: Backend - on click: launch image picker, then POST /issues/uploadAttachment/{issueId} via Supabase imageService
            // TODO: Backend - display selected image thumbnails here before submission
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .border(
                        1.dp,
                        AppColors.LightGray,
                        RoundedCornerShape(12.dp),
                    )
                    .clickable { /* TODO: launch image/file picker */ }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Camera,
                        contentDescription = "Add photo",
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(24.dp),
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Plus,
                            contentDescription = null,
                            tint = AppColors.Orange,
                            modifier = Modifier.size(10.dp),
                        )
                        Text(
                            text = "Add photo or file",
                            fontSize = 13.sp,
                            color = AppColors.Orange,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Text(
                        text = "Up to 5 images",
                        fontSize = 11.sp,
                        color = AppColors.TextHint,
                    )
                }
            }
        }

        item(key = "submit") {
            Spacer(modifier = Modifier.height(4.dp))
            val isValid = description.isNotBlank() && vehiclePlate.isNotBlank() &&
                    selectedCategory != null && selectedSeverity != null
            Button(
                onClick = {
                    // TODO: Backend - POST /issues/create with:
                    //   { description, severity: selectedSeverity, vehiclePlate, category: selectedCategory, mechanicId }
                    // On success: show submissionSuccess = true; On error: show error dialog
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
                    text = "Submit Issue",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun IssueSubmissionSuccess(onCreateAnother: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.White)
            .topSafeDrawingPadding()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(AppColors.Green, RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Image,
                contentDescription = null,
                tint = AppColors.White,
                modifier = Modifier.size(30.dp),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Issue Reported!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.FontBlackStrong,
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Your issue has been submitted and assigned to the lead mechanic for review.",
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
            Text("Report Another Issue", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun IssueCreationScreenPreview() {
    IssueCreationScreen()
}
