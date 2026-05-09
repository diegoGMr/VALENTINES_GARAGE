package com.msn.valentinesgarage.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeViewModel
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
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Tasks
import compose.icons.fontawesomeicons.solid.User

enum class HomeBottomTab(
    val title: String,
    val icon: ImageVector,
) {
    Home(title = "Home", icon = FontAwesomeIcons.Solid.Home),
    Booking(title = "Booking", icon = FontAwesomeIcons.Solid.Book),
    Issues(title = "Issues", icon = FontAwesomeIcons.Solid.Tasks),
    Admin(title = "Admin", icon = FontAwesomeIcons.Solid.User),
    Settings(title = "Settings", icon = FontAwesomeIcons.Solid.Cog),
}

private enum class HomeScreenDestination {
    Dashboard,
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
    val state by homeViewModel.state.collectAsState()

    val tabs = remember(role) {
        buildList {
            add(HomeBottomTab.Home)
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
                                HomeBottomTab.Booking -> HomeScreenDestination.Booking
                                HomeBottomTab.Issues -> HomeScreenDestination.Issues
                                HomeBottomTab.Settings -> HomeScreenDestination.Settings
                                HomeBottomTab.Admin -> HomeScreenDestination.Admin
                                else -> currentScreen
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

                        item {
                            SectionLabel(
                                text = if (role == "mechanic") "Unassigned Bookings" else "Active Issues",
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                        }

                        if (role == "mechanic") {
                            val unassigned = state.bookings?.filter { it.clientId != null } ?: emptyList()
                            items(items = unassigned, key = { "booking_${it.id}" }) { booking ->
                                HomeVehicleTaskCard(
                                    imageUrl = "",
                                    title = "Booking #${booking.id}",
                                    subtitle = "Date: ${booking.date} at ${booking.time}",
                                    dateText = "Pending Assignment",
                                    pendingTasksText = "Claim",
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    onClick = { 
                                        homeViewModel.assignToBooking(token, booking.id, role, userId)
                                    },
                                )
                            }
                        } else {
                            items(items = state.issues, key = { it.id }) { issue ->
                                HomeVehicleTaskCard(
                                    imageUrl = "",
                                    title = "Issue #${issue.id}",
                                    subtitle = issue.description,
                                    dateText = "Pending",
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

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
