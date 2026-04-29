package com.msn.valentinesgarage.screens.client

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import compose.icons.fontawesomeicons.solid.Calendar
import compose.icons.fontawesomeicons.solid.Check
import compose.icons.fontawesomeicons.solid.Clock

// UI model for a bookable time slot (3 per day: morning, afternoon, evening)
data class TimeSlotUi(
    val id: String,
    val label: String,      // e.g. "09:00 AM"
    val period: String,     // "Morning", "Afternoon", "Evening"
    val isAvailable: Boolean,
)

// UI model for appointment status step in the progress tracker
data class AppointmentStatusStep(
    val label: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
)

@Composable
fun AppointmentBookingScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: Backend - GET /appointments/availableSlots?date={selectedDate} to load available time slots
    // TODO: Backend - POST /appointments/book with { clientId, slotId, vehiclePlate, notes } to create booking
    // TODO: Backend - GET /appointments/status/{appointmentId} to poll live appointment progress
    // TODO: Backend - subscribe to push notifications for appointment status updates (Firebase/OneSignal)

    var selectedDateIndex by remember { mutableStateOf(0) }
    var selectedSlotId by remember { mutableStateOf<String?>(null) }
    var vehiclePlate by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var bookingConfirmed by remember { mutableStateOf(false) }

    val dateOptions = listOf("Today", "Tomorrow", "In 2 days", "In 3 days", "In 4 days")

    // Sample slots — replace with API response
    val timeSlots = listOf(
        TimeSlotUi("slot_1", "09:00 AM", "Morning", isAvailable = true),
        TimeSlotUi("slot_2", "01:00 PM", "Afternoon", isAvailable = true),
        TimeSlotUi("slot_3", "04:00 PM", "Evening", isAvailable = false),
    )

    // Sample progress steps for a booked appointment
    val progressSteps = listOf(
        AppointmentStatusStep("Booking Confirmed", isCompleted = true, isCurrent = false),
        AppointmentStatusStep("Mechanic Assigned", isCompleted = true, isCurrent = false),
        AppointmentStatusStep("In Progress", isCompleted = false, isCurrent = true),
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

    LazyColumn(
        modifier = modifier
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
            // TODO: Backend - replace static dateOptions with real available dates from GET /appointments/availableDates
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
                            text = dateOptions[index],
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
                text = "Available Time Slots",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Backend - load slots from GET /appointments/availableSlots?date={selectedDate}
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                timeSlots.forEach { slot ->
                    TimeSlotCard(
                        slot = slot,
                        isSelected = selectedSlotId == slot.id,
                        onSelect = { if (slot.isAvailable) selectedSlotId = slot.id },
                    )
                }
            }
        }

        item(key = "vehicle_section") {
            SectionLabel(
                text = "Vehicle Details",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Backend - populate vehicle plate options from GET /truck/getClientTrucks/{clientId}
            OutlinedTextField(
                value = vehiclePlate,
                onValueChange = { vehiclePlate = it },
                placeholder = {
                    Text("Vehicle plate number", color = AppColors.TextHint, fontSize = 14.sp)
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
        }

        item(key = "notes_section") {
            SectionLabel(
                text = "Additional Notes",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = {
                    Text("Describe the issue (optional)", color = AppColors.TextHint, fontSize = 14.sp)
                },
                minLines = 3,
                maxLines = 5,
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

        item(key = "submit_button") {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    // TODO: Backend - POST /appointments/book with { clientId, slotId: selectedSlotId, vehiclePlate, notes }
                    // On success: set bookingConfirmed = true and store returned appointmentId
                    // On error: show error dialog
                    if (selectedSlotId != null && vehiclePlate.isNotBlank()) {
                        bookingConfirmed = true
                    }
                },
                enabled = selectedSlotId != null && vehiclePlate.isNotBlank(),
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
        if (!slot.isAvailable) {
            Text(
                text = "Unavailable",
                fontSize = 12.sp,
                color = AppColors.TextHint,
                fontWeight = FontWeight.Medium,
            )
        } else if (isSelected) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(AppColors.Orange, RoundedCornerShape(50.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Check,
                    contentDescription = "Selected",
                    tint = AppColors.White,
                    modifier = Modifier.size(10.dp),
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
    // TODO: Backend - poll GET /appointments/status/{appointmentId} every 30s to refresh progress steps
    // TODO: Backend - or use WebSocket/FCM push notification to update steps in real time

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
        // Timeline column: circle + vertical line
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
