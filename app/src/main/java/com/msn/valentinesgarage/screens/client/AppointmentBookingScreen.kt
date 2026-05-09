package com.msn.valentinesgarage.screens.client

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
import com.msn.valentinesgarage.screens.client.viewmodels.BookingViewModel
import com.msn.valentinesgarage.screens.client.viewmodels.VehicleViewModel
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*
import java.time.LocalDate

// UI model for a bookable time slot
data class TimeSlotUi(
    val id: String,
    val label: String,      // e.g. "09:00 AM"
    val period: String,     // "Morning", "Afternoon", "Evening"
    val isAvailable: Boolean,
)

// UI model for appointment status step
data class AppointmentStatusStep(
    val label: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    token: String = "",
    userId: Int = 0,
    modifier: Modifier = Modifier,
    bookingViewModel: BookingViewModel = viewModel(),
    vehicleViewModel: VehicleViewModel = viewModel()
) {
    val uiState by bookingViewModel.uiState.collectAsState()
    val vehicleState by vehicleViewModel.uiState.collectAsState()

    var selectedDateIndex by remember { mutableStateOf(0) }
    val dateOptions = remember {
        (0..4).map { LocalDate.now().plusDays(it.toLong()).toString() }
    }

    var selectedVehicleId by remember { mutableStateOf<Int?>(null) }
    var bookingTime by remember { mutableStateOf("09:00") }
    var clientNotes by remember { mutableStateOf("") }
    var bookingConfirmed by remember { mutableStateOf(false) }
    var showRegistration by remember { mutableStateOf(false) }

    LaunchedEffect(token, selectedDateIndex) {
        if (token.isNotEmpty()) {
            bookingViewModel.loadBookings(token, dateOptions[selectedDateIndex])
            vehicleViewModel.loadData(token)
        }
    }

    if (showRegistration) {
        VehicleRegistrationScreen(
            token = token,
            userId = userId,
            onSuccess = {
                showRegistration = false
                vehicleViewModel.loadData(token)
            },
            modifier = modifier
        )
        return
    }

    LaunchedEffect(uiState.bookingSuccess) {
        if (uiState.bookingSuccess) {
            bookingConfirmed = true
            bookingViewModel.resetSuccess()
        }
    }

    // Map database bookings to UI slots
    val occupiedSlots = uiState.bookings.map { b ->
        TimeSlotUi(
            id = b.id.toString(),
            label = b.time ?: "TBD",
            period = "Booked",
            isAvailable = false
        )
    }

    val progressSteps = listOf(
        AppointmentStatusStep("Booking Confirmed", isCompleted = true, isCurrent = false),
        AppointmentStatusStep("Mechanic Assigned", isCompleted = false, isCurrent = true),
        AppointmentStatusStep("In Progress", isCompleted = false, isCurrent = false),
        AppointmentStatusStep("Review & QA", isCompleted = false, isCurrent = false),
        AppointmentStatusStep("Completed", isCompleted = false, isCurrent = false),
    )

    if (bookingConfirmed) {
        BookingConfirmationView(
            progressSteps = progressSteps,
            onBookAnother = { bookingConfirmed = false },
            modifier = modifier,
        )
        return
    }

    PullToRefreshBox(
        isRefreshing = uiState.isLoading || vehicleState.isLoading,
        onRefresh = {
            bookingViewModel.loadBookings(token, dateOptions[selectedDateIndex])
            vehicleViewModel.loadData(token)
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
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
                        imageVector = FontAwesomeIcons.Solid.Calendar,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Book Appointment",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.FontBlackStrong,
                    )
                }
            }

            item(key = "date_section") {
                SectionLabel(
                    text = "Select Date",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(dateOptions.size) { index ->
                        val isSelected = selectedDateIndex == index
                        Box(
                            modifier = Modifier
                                .border(
                                    width = 1.5.dp,
                                    color = if (isSelected) AppColors.Orange else AppColors.LightGray,
                                    shape = RoundedCornerShape(10.dp),
                                )
                                .background(
                                    if (isSelected) AppColors.Orange else AppColors.White,
                                    RoundedCornerShape(10.dp),
                                )
                                .clickable { selectedDateIndex = index }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                        ) {
                            Text(
                                text = if (index == 0) "Today" else dateOptions[index],
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isSelected) AppColors.White else AppColors.FontBlackMedium,
                            )
                        }
                    }
                }
            }

            item(key = "slots_section") {
                SectionLabel(
                    text = "Existing Bookings",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (occupiedSlots.isEmpty() && !uiState.isLoading) {
                        Text(
                            text = "No appointments booked for this date",
                            fontSize = 14.sp,
                            color = AppColors.TextHint,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    occupiedSlots.forEach { slot ->
                        TimeSlotCard(
                            slot = slot,
                            isSelected = false,
                            onSelect = { },
                        )
                    }
                }
            }

            item(key = "booking_form") {
                SectionLabel(
                    text = "Select Vehicle",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (vehicleState.myTrucks.isEmpty() && !vehicleState.isLoading) {
                        Button(
                            onClick = { showRegistration = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.OrangeWhite, contentColor = AppColors.Orange)
                        ) {
                            Icon(FontAwesomeIcons.Solid.Plus, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Register Your First Vehicle")
                        }
                    } else {
                        var vehicleExpanded by remember { mutableStateOf(false) }
                        Box {
                            OutlinedTextField(
                                value = vehicleState.myTrucks.find { it.truck_id == selectedVehicleId }?.plate_number ?: "Choose Vehicle",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    IconButton(onClick = { vehicleExpanded = true }) {
                                        Icon(FontAwesomeIcons.Solid.ArrowDown, null, modifier = Modifier.size(16.dp))
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.Orange,
                                    unfocusedBorderColor = AppColors.LightGray
                                )
                            )
                            DropdownMenu(expanded = vehicleExpanded, onDismissRequest = { vehicleExpanded = false }) {
                                vehicleState.myTrucks.forEach { truck ->
                                    DropdownMenuItem(
                                        text = { Text("${truck.plate_number}") },
                                        onClick = {
                                            selectedVehicleId = truck.truck_id
                                            vehicleExpanded = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("Register New Vehicle") },
                                    onClick = {
                                        showRegistration = true
                                        vehicleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = bookingTime,
                        onValueChange = { bookingTime = it },
                        label = { Text("Booking Time (HH:mm)") },
                        placeholder = { Text("e.g. 09:00 or 15:30") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Orange,
                            unfocusedBorderColor = AppColors.LightGray
                        )
                    )

                    OutlinedTextField(
                        value = clientNotes,
                        onValueChange = { clientNotes = it },
                        label = { Text("Client Notes (Optional)") },
                        placeholder = { Text("e.g. Please check the brakes specifically.") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AppColors.Orange,
                            unfocusedBorderColor = AppColors.LightGray
                        )
                    )
                }
            }

            item(key = "submit_button") {
                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = {
                        selectedVehicleId?.let { vId ->
                            bookingViewModel.createBooking(
                                token,
                                userId,
                                vId,
                                dateOptions[selectedDateIndex],
                                bookingTime,
                                clientNotes.ifBlank { null }
                            )
                        }
                    },
                    enabled = !uiState.isLoading && selectedVehicleId != null,
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
                        text = "Confirm Booking",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSlotCard(
    slot: TimeSlotUi,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    val borderColor = when {
        isSelected -> AppColors.Orange
        !slot.isAvailable -> AppColors.LightGray
        else -> AppColors.LightGray
    }
    val bgColor = when {
        isSelected -> AppColors.OrangeWhite
        else -> AppColors.White
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor, RoundedCornerShape(12.dp))
            .clickable(enabled = slot.isAvailable, onClick = onSelect)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = FontAwesomeIcons.Solid.Clock,
                contentDescription = null,
                tint = if (slot.isAvailable) AppColors.Orange else AppColors.LightGray,
                modifier = Modifier.size(16.dp),
            )
            Column {
                Text(
                    text = slot.label,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (slot.isAvailable) AppColors.FontBlackStrong else AppColors.TextHint,
                )
                Text(
                    text = slot.period,
                    fontSize = 12.sp,
                    color = AppColors.TextHint,
                )
            }
        }
    }
}

@Composable
private fun BookingConfirmationView(
    progressSteps: List<AppointmentStatusStep>,
    onBookAnother: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item(key = "confirmed_header") {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(AppColors.Green, RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Check,
                    contentDescription = "Confirmed",
                    tint = AppColors.White,
                    modifier = Modifier.size(28.dp),
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Booking Confirmed!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.FontBlackStrong,
            )
            Text(
                text = "Track your appointment progress below",
                fontSize = 13.sp,
                color = AppColors.TextHint,
            )
        }

        item(key = "progress_tracker") {
            SectionLabel(text = "Appointment Progress")
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                ) {
                    progressSteps.forEachIndexed { index, step ->
                        AppointmentProgressStep(
                            step = step,
                            isLast = index == progressSteps.lastIndex,
                        )
                    }
                }
            }
        }

        item(key = "book_another") {
            Button(
                onClick = onBookAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Orange,
                    contentColor = AppColors.White,
                ),
            ) {
                Text(
                    text = "Book Another Appointment",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun AppointmentProgressStep(
    step: AppointmentStatusStep,
    isLast: Boolean,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        when {
                            step.isCompleted -> AppColors.Green
                            step.isCurrent -> AppColors.Orange
                            else -> AppColors.LightGray
                        },
                        RoundedCornerShape(50.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (step.isCompleted) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Check,
                        contentDescription = null,
                        tint = AppColors.White,
                        modifier = Modifier.size(10.dp),
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (step.isCompleted) AppColors.Green else AppColors.LightGray),
                )
            }
        }

        Text(
            text = step.label,
            fontSize = 14.sp,
            fontWeight = if (step.isCurrent) FontWeight.SemiBold else FontWeight.Normal,
            color = when {
                step.isCurrent -> AppColors.Orange
                step.isCompleted -> AppColors.FontBlackStrong
                else -> AppColors.TextHint
            },
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun AppointmentBookingScreenPreview() {
    AppointmentBookingScreen()
}
