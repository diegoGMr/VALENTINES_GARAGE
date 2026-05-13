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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.msn.valentinesgarage.theme.AppColors
import com.msn.valentinesgarage.theme.ConfigureSystemBars
import com.msn.valentinesgarage.theme.topSafeDrawingPadding
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Calendar
import compose.icons.fontawesomeicons.solid.Clock
import compose.icons.fontawesomeicons.solid.Times
import compose.icons.fontawesomeicons.solid.Truck

// UI model for displaying a single appointment
data class AppointmentUi(
    val id: String,
    val date: String,
    val time: String,
    val vehiclePlate: String,
    val vehicleModel: String,
    val status: AppointmentStatus,
    val notes: String = "",
)

enum class AppointmentStatus(val label: String, val color: androidx.compose.ui.graphics.Color) {
    Upcoming("Upcoming", AppColors.Orange),
    InProgress("In Progress", AppColors.Green),
    Completed("Completed", AppColors.FontBlackSoft),
    Cancelled("Cancelled", AppColors.Red),
}

private enum class AppointmentsTab { Upcoming, History }

@Composable
fun AppointmentsListScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: Backend - GET /appointments/getClientAppointments/{clientId} to load all appointments
    // TODO: Backend - DELETE /appointments/cancel/{appointmentId} to cancel an appointment
    // TODO: Backend - PUT /appointments/reschedule/{appointmentId} with { newSlotId } to reschedule

    var selectedTab by remember { mutableStateOf(AppointmentsTab.Upcoming) }

    ConfigureSystemBars(statusBarColor = AppColors.White)

    // Sample data — replace with API response
    val allAppointments = listOf(
        AppointmentUi("a1", "20 April 2026", "09:00 AM", "HSD343", "Scania Railer", AppointmentStatus.Upcoming),
        AppointmentUi("a2", "22 April 2026", "01:00 PM", "JKL918", "Volvo FH16", AppointmentStatus.Upcoming),
        AppointmentUi("a3", "15 March 2026", "04:00 PM", "HSD343", "Scania Railer", AppointmentStatus.Completed, "Brake pads replaced"),
        AppointmentUi("a4", "10 March 2026", "09:00 AM", "QTR552", "MAN TGX", AppointmentStatus.Cancelled),
        AppointmentUi("a5", "01 March 2026", "01:00 PM", "JKL918", "Volvo FH16", AppointmentStatus.Completed, "Oil change & full service"),
    )

    val upcoming = allAppointments.filter {
        it.status == AppointmentStatus.Upcoming || it.status == AppointmentStatus.InProgress
    }
    val history = allAppointments.filter {
        it.status == AppointmentStatus.Completed || it.status == AppointmentStatus.Cancelled
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.White)
            .topSafeDrawingPadding(),
    ) {
        // Header
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
                text = "My Appointments",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.FontBlackStrong,
            )
        }

        // Tab selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppointmentsTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .border(
                            width = 1.5.dp,
                            color = if (isSelected) AppColors.Orange else AppColors.LightGray,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .background(
                            if (isSelected) AppColors.Orange else AppColors.White,
                            RoundedCornerShape(10.dp),
                        )
                        .clickable { selectedTab = tab }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = tab.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) AppColors.White else AppColors.FontBlackMedium,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val displayList = if (selectedTab == AppointmentsTab.Upcoming) upcoming else history

        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (selectedTab == AppointmentsTab.Upcoming)
                        "No upcoming appointments"
                    else
                        "No past appointments",
                    fontSize = 14.sp,
                    color = AppColors.TextHint,
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(displayList, key = { it.id }) { appointment ->
                    AppointmentCard(
                        appointment = appointment,
                        onCancel = {
                            // TODO: Backend - call DELETE /appointments/cancel/{appointmentId}
                            // On success: refresh appointment list
                        },
                        onReschedule = {
                            // TODO: Backend - navigate to booking screen with pre-filled vehicle, passing existing appointmentId for reschedule flow
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: AppointmentUi,
    onCancel: () -> Unit,
    onReschedule: () -> Unit,
) {
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
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Truck,
                            contentDescription = null,
                            tint = AppColors.Orange,
                            modifier = Modifier.size(12.dp),
                        )
                        Text(
                            text = appointment.vehiclePlate,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.FontBlackStrong,
                        )
                    }
                    Text(
                        text = appointment.vehicleModel,
                        fontSize = 12.sp,
                        color = AppColors.TextHint,
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .background(
                            appointment.status.color.copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = appointment.status.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = appointment.status.color,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Calendar,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(11.dp),
                    )
                    Text(text = appointment.date, fontSize = 12.sp, color = AppColors.TextHint)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Clock,
                        contentDescription = null,
                        tint = AppColors.TextHint,
                        modifier = Modifier.size(11.dp),
                    )
                    Text(text = appointment.time, fontSize = 12.sp, color = AppColors.TextHint)
                }
            }

            if (appointment.notes.isNotBlank()) {
                Text(
                    text = appointment.notes,
                    fontSize = 12.sp,
                    color = AppColors.FontBlackSoft,
                    maxLines = 2,
                )
            }

            if (appointment.status == AppointmentStatus.Upcoming) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Reschedule button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, AppColors.Orange, RoundedCornerShape(10.dp))
                            .clickable(onClick = onReschedule)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Reschedule",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.Orange,
                        )
                    }
                    // Cancel button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, AppColors.Red, RoundedCornerShape(10.dp))
                            .clickable(onClick = onCancel)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Times,
                                contentDescription = null,
                                tint = AppColors.Red,
                                modifier = Modifier.size(10.dp),
                            )
                            Text(
                                text = "Cancel",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppColors.Red,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun AppointmentsListScreenPreview() {
    AppointmentsListScreen()
}
