package com.msn.valentinesgarage.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeViewModel
import com.msn.valentinesgarage.data.models.*
import com.msn.valentinesgarage.screens.admin.AdminDashboardScreen
import com.msn.valentinesgarage.screens.client.AppointmentBookingScreen
import com.msn.valentinesgarage.screens.dialog.FullLoadingScreen
import com.msn.valentinesgarage.screens.home.composables.HomeHeaderBanner
import com.msn.valentinesgarage.screens.home.composables.HomeVehicleTaskCard
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.screens.mechanic.IssuesListScreen
import com.msn.valentinesgarage.screens.settings.SettingsScreen
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Book
import compose.icons.fontawesomeicons.solid.Car
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Tasks
import compose.icons.fontawesomeicons.solid.User
import com.msn.valentinesgarage.screens.mechanic.MechanicVehiclesScreen
import com.msn.valentinesgarage.screens.mechanic.viewmodels.MechanicVehiclesViewModel

enum class HomeBottomTab(
    val title: String,
    val icon: ImageVector,
) {
    Home(title = "Home", icon = FontAwesomeIcons.Solid.Home),
    Vehicles(title = "Vehicles", icon = FontAwesomeIcons.Solid.Car),
    Booking(title = "Booking", icon = FontAwesomeIcons.Solid.Book),
    Issues(title = "Issues", icon = FontAwesomeIcons.Solid.Tasks),
    Admin(title = "Admin", icon = FontAwesomeIcons.Solid.User),
    Settings(title = "Settings", icon = FontAwesomeIcons.Solid.Cog),
}

private enum class HomeScreenDestination {
    Dashboard,
    Vehicles,
    Booking,
    Issues,
    VehicleInformation,
    Settings,
    Admin,
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    token: String = "",
    userId: Int = 0,
    role: String = "mechanic",
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
) {
    var selectedTab by remember { mutableStateOf(HomeBottomTab.Home) }
    var currentScreen by remember { mutableStateOf(HomeScreenDestination.Dashboard) }
    var selectedBookingForClaim by remember { mutableStateOf<Booking?>(null) }
    val state by homeViewModel.state.collectAsState()

    if (selectedBookingForClaim != null) {
        BookingClaimDialog(
            booking = selectedBookingForClaim!!,
            onDismiss = { selectedBookingForClaim = null },
            onClaim = {
                homeViewModel.assignToBooking(token, selectedBookingForClaim!!.id, role, userId)
                selectedBookingForClaim = null
            }
        )
    }

    val tabs = remember(role) {
        buildList {
            add(HomeBottomTab.Home)
            if (role == "mechanic") add(HomeBottomTab.Vehicles)
            if (role != "mechanic") add(HomeBottomTab.Booking)
            add(HomeBottomTab.Issues)
            if (role == "admin") add(HomeBottomTab.Admin)
            add(HomeBottomTab.Settings)
        }
    }

    LaunchedEffect(token, role, userId) {
        if (token.isNotEmpty()) {
            homeViewModel.loadData(token, role, userId)
        }
    }

    if (state.isLoading && state.user == null) {
        FullLoadingScreen(message = "Fetching Data...")
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.White,
        bottomBar = {
            NavigationBar(containerColor = AppColors.White) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            currentScreen = when (tab) {
                                HomeBottomTab.Home -> HomeScreenDestination.Dashboard
                                HomeBottomTab.Vehicles -> HomeScreenDestination.Vehicles
                                HomeBottomTab.Booking -> HomeScreenDestination.Booking
                                HomeBottomTab.Issues -> HomeScreenDestination.Issues
                                HomeBottomTab.Settings -> HomeScreenDestination.Settings
                                HomeBottomTab.Admin -> HomeScreenDestination.Admin
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                modifier = Modifier.size(16.dp),
                                contentDescription = tab.title,
                                tint = if (selectedTab == tab) AppColors.Orange else AppColors.TextHint,
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                color = if (selectedTab == tab) AppColors.Orange else AppColors.TextHint,
                                fontSize = 10.sp,
                            )
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (currentScreen) {
            HomeScreenDestination.Dashboard -> {
                // ... (no changes needed here after the items update above)
                PullToRefreshBox(
                    isRefreshing = state.isLoading,
                    onRefresh = { homeViewModel.loadData(token, role, userId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        item(key = "home_banner") {
                            HomeHeaderBanner(
                                imageRes = R.drawable.truckbanner,
                                profileImageRes = R.drawable.defaultprofileicon,
                                greetingText = "Welcome Back!",
                                profileName = state.user?.full_name ?: "Loading...",
                                notificationCount = 5,
                            )
                        }

                        if (role == "mechanic") {
                            item {
                                SectionLabel(
                                    text = "My Assigned Vehicles",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                            
                            items(items = state.mechanicVisits, key = { "visit_${it.visitId}" }) { visit ->
                                val plate = visit.truck?.plate_number ?: "Unknown"
                                val clientName = visit.client?.full_name ?: "Unknown"
                                
                                HomeVehicleTaskCard(
                                    imageUrl = "",
                                    title = "Vehicle: $plate",
                                    subtitle = "Client: $clientName",
                                    dateText = "Active Visit",
                                    pendingTasksText = "View Info",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { 
                                        selectedTab = HomeBottomTab.Vehicles
                                        currentScreen = HomeScreenDestination.Vehicles 
                                    },
                                )
                            }

                            item {
                                SectionLabel(
                                    text = "Unassigned Bookings",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }

                            val unassigned = state.bookings?.filter { 
                                it.clientId != null && (it.visit == null || it.visit.isEmpty())
                            } ?: emptyList()
                            items(items = unassigned, key = { "booking_${it.id}" }) { booking ->
                                val plate = booking.truck?.plate_number ?: "Vehicle #${booking.truckId}"
                                val specialty = booking.truck?.speciality?.name ?: ""
                                val clientName = booking.client?.full_name ?: "Client #${booking.clientId}"
                                
                                HomeVehicleTaskCard(
                                    imageUrl = "",
                                    title = "Booking: $plate" + (if (specialty.isNotEmpty()) " ($specialty)" else ""),
                                    subtitle = "Client: $clientName\nDate: ${booking.date} at ${booking.time}",
                                    dateText = "Pending",
                                    pendingTasksText = "Claim",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { selectedBookingForClaim = booking },
                                )
                            }
                        } else {
                            items(items = state.issues, key = { it.id }) { issue ->
                                val statusText = if (issue.resolved == true) "Resolved" else "Pending"
                                HomeVehicleTaskCard(
                                    imageUrl = "",
                                    title = "Issue #${issue.id}",
                                    subtitle = issue.description,
                                    dateText = statusText,
                                    pendingTasksText = "Details",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { currentScreen = HomeScreenDestination.VehicleInformation },
                                )
                            }
                        }

                        state.error?.let { error ->
                            item {
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        if (!state.isLoading && state.issues.isEmpty() && state.error == null) {
                            item {
                                Text(
                                    text = "No issues found in database",
                                    modifier = Modifier.padding(16.dp),
                                    color = AppColors.TextHint
                                )
                            }
                        }
                    }
                }
            }

            HomeScreenDestination.Vehicles -> {
                MechanicVehiclesScreen(
                    token = token,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }

            HomeScreenDestination.VehicleInformation -> {
                VehicleInformationScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }

            HomeScreenDestination.Booking -> {
                AppointmentBookingScreen(
                    token = token,
                    userId = userId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }

            HomeScreenDestination.Issues -> {
                IssuesListScreen(
                    token = token,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }

            HomeScreenDestination.Settings -> {
                SettingsScreen(
                    user = state.user,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    onLogout = onLogout
                )
            }

            HomeScreenDestination.Admin -> {
                AdminDashboardScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    token = token,
                )
            }
        }
    }
}

@Composable
private fun BookingClaimDialog(
    booking: Booking,
    onDismiss: () -> Unit,
    onClaim: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Claim Booking for ${booking.truck?.plate_number ?: "#${booking.id}"}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Client: ${booking.client?.full_name ?: "Unknown"}", fontWeight = FontWeight.Medium)
                Text("Date: ${booking.date}")
                Text("Time: ${booking.time}")
                booking.truck?.speciality?.name?.let {
                    Text("Specialty: $it")
                }
                if (!booking.clientNotes.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Client Notes:", fontWeight = FontWeight.SemiBold)
                    Text(booking.clientNotes, fontSize = 13.sp, color = AppColors.FontBlackMedium)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Do you want to assign yourself to this vehicle?", fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(
                onClick = onClaim,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange)
            ) {
                Text("Claim & Create Visit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
