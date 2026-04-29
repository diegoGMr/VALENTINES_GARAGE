package com.msn.valentinesgarage.screens.admin

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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.ExclamationTriangle
import compose.icons.fontawesomeicons.solid.Tasks
import compose.icons.fontawesomeicons.solid.Truck
import compose.icons.fontawesomeicons.solid.User

// UI model for a stat card on the admin overview
data class AdminStatUi(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
)

// UI model for mechanic workload row
data class MechanicWorkloadUi(
    val id: String,
    val name: String,
    val role: String,
    val openTasks: Int,
    val completedToday: Int,
)

// UI model for an appointment row in admin view
data class AdminAppointmentUi(
    val id: String,
    val clientName: String,
    val vehiclePlate: String,
    val date: String,
    val time: String,
    val status: String,
    val statusColor: Color,
)

private enum class AdminFilter(val label: String) {
    All("All"),
    Upcoming("Upcoming"),
    InProgress("In Progress"),
    Completed("Completed"),
}

@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier,
) {
    // TODO: Backend - GET /admin/stats to load system overview numbers (total trucks, mechanics, open issues, today's appointments)
    // TODO: Backend - GET /admin/appointments?status={filter} to load filtered appointments list
    // TODO: Backend - GET /admin/mechanics/workload to load all mechanics with their task counts
    // TODO: Backend - GET /admin/database/stats for database statistics (total records, storage)

    var selectedFilter by remember { mutableStateOf(AdminFilter.All) }

    // Sample stats — replace with API response
    val stats = listOf(
        AdminStatUi("Total Trucks", "24", FontAwesomeIcons.Solid.Truck, AppColors.Orange),
        AdminStatUi("Mechanics", "8", FontAwesomeIcons.Solid.User, AppColors.Green),
        AdminStatUi("Open Issues", "13", FontAwesomeIcons.Solid.ExclamationTriangle, AppColors.Red),
        AdminStatUi("Today's Appointments", "6", FontAwesomeIcons.Solid.Calendar, AppColors.Pink),
    )

    // Sample mechanics workload — replace with API response
    val mechanics = listOf(
        MechanicWorkloadUi("m1", "Robert Mountain", "Lead Mechanic", openTasks = 5, completedToday = 3),
        MechanicWorkloadUi("m2", "Simon Rivers", "Mechanic", openTasks = 3, completedToday = 2),
        MechanicWorkloadUi("m3", "Joseph Oceanside", "Mechanic", openTasks = 7, completedToday = 1),
        MechanicWorkloadUi("m4", "Jonas Desert", "Inspector", openTasks = 2, completedToday = 4),
    )

    // Sample appointments — replace with API response
    val appointments = listOf(
        AdminAppointmentUi("ap1", "David Andrew", "HSD343", "29 Apr 2026", "09:00 AM", "Upcoming", AppColors.Orange),
        AdminAppointmentUi("ap2", "Sarah Connor", "JKL918", "29 Apr 2026", "01:00 PM", "In Progress", AppColors.Green),
        AdminAppointmentUi("ap3", "Mike Johnson", "QTR552", "29 Apr 2026", "04:00 PM", "Upcoming", AppColors.Orange),
        AdminAppointmentUi("ap4", "Anna Smith", "BRT001", "28 Apr 2026", "09:00 AM", "Completed", AppColors.FontBlackSoft),
    )

    val filteredAppointments = when (selectedFilter) {
        AdminFilter.All -> appointments
        AdminFilter.Upcoming -> appointments.filter { it.status == "Upcoming" }
        AdminFilter.InProgress -> appointments.filter { it.status == "In Progress" }
        AdminFilter.Completed -> appointments.filter { it.status == "Completed" }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.OrangeWhite)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = "header") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Admin Dashboard",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.FontBlackStrong,
                    )
                    Text(
                        text = "System overview",
                        fontSize = 13.sp,
                        color = AppColors.TextHint,
                    )
                }
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Cog,
                    contentDescription = "Settings",
                    tint = AppColors.FontBlackSoft,
                    modifier = Modifier.size(18.dp),
                )
            }
        }

        // Stats grid
        item(key = "stats") {
            SectionLabel(
                text = "Overview",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Backend - refresh stats on pull-to-refresh (GET /admin/stats)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                stats.take(2).forEach { stat ->
                    AdminStatCard(stat = stat, modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                stats.drop(2).forEach { stat ->
                    AdminStatCard(stat = stat, modifier = Modifier.weight(1f))
                }
            }
        }

        // Appointments with filter
        item(key = "appointments_header") {
            SectionLabel(
                text = "All Appointments",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(AdminFilter.entries) { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .border(
                                1.5.dp,
                                if (isSelected) AppColors.Orange else AppColors.LightGray,
                                RoundedCornerShape(8.dp),
                            )
                            .background(
                                if (isSelected) AppColors.Orange else AppColors.White,
                                RoundedCornerShape(8.dp),
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text(
                            text = filter.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) AppColors.White else AppColors.FontBlackMedium,
                        )
                    }
                }
            }
        }

        items(filteredAppointments, key = { it.id }) { appt ->
            AdminAppointmentRow(
                appointment = appt,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // Mechanics workload
        item(key = "mechanics_header") {
            SectionLabel(
                text = "Mechanics Workload",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        items(mechanics, key = { it.id }) { mechanic ->
            MechanicWorkloadRow(
                mechanic = mechanic,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
        }

        // Database stats placeholder
        item(key = "db_stats") {
            SectionLabel(
                text = "Database Statistics",
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
            // TODO: Backend - GET /admin/database/stats to load real numbers
            Card(
                modifier = Modifier.padding(horizontal = 16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DbStatRow(label = "Total Users", value = "42")
                    DbStatRow(label = "Total Trucks", value = "24")
                    DbStatRow(label = "Total Issues", value = "187")
                    DbStatRow(label = "Total Tasks", value = "340")
                    DbStatRow(label = "Total Appointments", value = "95")
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(
    stat: AdminStatUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
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
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(stat.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = stat.color,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = stat.value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.FontBlackStrong,
            )
            Text(
                text = stat.label,
                fontSize = 11.sp,
                color = AppColors.TextHint,
            )
        }
    }
}

@Composable
private fun AdminAppointmentRow(
    appointment: AdminAppointmentUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointment.clientName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.FontBlackStrong,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Truck,
                            contentDescription = null,
                            tint = AppColors.TextHint,
                            modifier = Modifier.size(10.dp),
                        )
                        Text(text = appointment.vehiclePlate, fontSize = 12.sp, color = AppColors.TextHint)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = FontAwesomeIcons.Solid.Clock,
                            contentDescription = null,
                            tint = AppColors.TextHint,
                            modifier = Modifier.size(10.dp),
                        )
                        Text(text = appointment.time, fontSize = 12.sp, color = AppColors.TextHint)
                    }
                }
                Text(text = appointment.date, fontSize = 11.sp, color = AppColors.TextHint)
            }
            Box(
                modifier = Modifier
                    .background(appointment.statusColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            ) {
                Text(
                    text = appointment.status,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = appointment.statusColor,
                )
            }
        }
    }
}

@Composable
private fun MechanicWorkloadRow(
    mechanic: MechanicWorkloadUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AppColors.OrangeWhite, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.User,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(18.dp),
                    )
                }
                Column {
                    Text(
                        text = mechanic.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.FontBlackStrong,
                    )
                    Text(
                        text = mechanic.role,
                        fontSize = 12.sp,
                        color = AppColors.TextHint,
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Tasks,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(
                        text = "${mechanic.openTasks} open",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Orange,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = FontAwesomeIcons.Solid.Check,
                        contentDescription = null,
                        tint = AppColors.Green,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(
                        text = "${mechanic.completedToday} done today",
                        fontSize = 12.sp,
                        color = AppColors.Green,
                    )
                }
            }
        }
    }
}

@Composable
private fun DbStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, fontSize = 14.sp, color = AppColors.FontBlackSoft)
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.FontBlackStrong,
        )
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun AdminDashboardScreenPreview() {
    AdminDashboardScreen()
}
