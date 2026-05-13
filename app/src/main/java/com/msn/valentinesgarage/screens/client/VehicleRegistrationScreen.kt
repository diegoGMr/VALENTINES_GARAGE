package com.msn.valentinesgarage.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.screens.client.viewmodels.VehicleViewModel
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleRegistrationScreen(
    token: String,
    userId: Int,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VehicleViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ConfigureSystemBars(statusBarColor = AppColors.White)

    var plate by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedSpeciality by remember { mutableStateOf<Pair<Int, String>?>(null) }

    LaunchedEffect(token) {
        viewModel.loadData(token)
    }

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            viewModel.resetSuccess()
            onSuccess()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
            .topSafeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Register Your Vehicle",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.FontBlackStrong
            )
            Text(
                text = "Add your truck details to book appointments",
                fontSize = 14.sp,
                color = AppColors.TextHint
            )
        }

        item {
            SectionLabel(text = "Vehicle Information")
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = plate,
                onValueChange = { 
                    if (it.length <= 8) plate = it.uppercase() 
                },
                label = { Text("License Plate (Max 8 characters)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Orange,
                    unfocusedBorderColor = AppColors.LightGray
                )
            )
        }

        item {
            SectionLabel(text = "Truck Speciality")
            Spacer(modifier = Modifier.height(8.dp))
            
            Box {
                OutlinedTextField(
                    value = selectedSpeciality?.second ?: "Select Speciality",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            modifier = Modifier.clickable { expanded = true }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.Orange,
                        unfocusedBorderColor = AppColors.LightGray
                    )
                )
                
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    uiState.specialities.forEach { spec ->
                        DropdownMenuItem(
                            text = { Text(spec.second) },
                            onClick = {
                                selectedSpeciality = spec
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Button(
                onClick = {
                    selectedSpeciality?.let {
                        viewModel.registerVehicle(token, userId, plate, it.first)
                    }
                },
                enabled = !uiState.isLoading && plate.isNotBlank() && selectedSpeciality != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Orange,
                    contentColor = AppColors.White
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = AppColors.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register Vehicle", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
