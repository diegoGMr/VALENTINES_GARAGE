package com.msn.valentinesgarage.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeViewModel
import com.msn.valentinesgarage.screens.home.composables.HomeHeaderBanner
import com.msn.valentinesgarage.screens.home.composables.HomeVehicleTaskCard
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.screens.settings.SettingsScreen
import com.msn.valentinesgarage.screens.tasks.TasksScreen
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
    Tasks(title = "Tasks", icon = FontAwesomeIcons.Solid.Tasks),
    Admin(title = "Admin", icon = FontAwesomeIcons.Solid.User),
    Profile(title = "Profile", icon = FontAwesomeIcons.Solid.User),
    Settings(title = "Settings", icon = FontAwesomeIcons.Solid.Cog),
}

private enum class HomeScreenDestination {
    Dashboard,
    Tasks,
    VehicleInformation,
    Settings,
}

data class VehicleCardUi(
    val id: String,
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val dateText: String,
    val pendingTasksText: String,
)

val sampleVehicleCards = listOf(
    VehicleCardUi("1", "", "Truck A", "In progress", "2023-10-01", "2 tasks"),
    VehicleCardUi("2", "", "Truck B", "Pending", "2023-10-02", "5 tasks")
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    token: String = "",
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
            add(HomeBottomTab.Booking)
            add(HomeBottomTab.Issues)
            if (role == "lead_mechanic" || role == "mechanic" || role == "admin") add(HomeBottomTab.Tasks)
            if (role == "admin") add(HomeBottomTab.Admin)
            add(HomeBottomTab.Settings)
        }
    }

    LaunchedEffect(token, role) {
        homeViewModel.loadData(token, role)
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
                                HomeBottomTab.Tasks -> HomeScreenDestination.Tasks
                                HomeBottomTab.Settings -> HomeScreenDestination.Settings
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
                            )
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (currentScreen) {
            HomeScreenDestination.Dashboard -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(key = "home_banner") {
                        HomeHeaderBanner(
                            imageRes = R.drawable.truckbanner,
                            profileImageRes = R.drawable.defaultprofileicon,
                            greetingText = "Welcome Back!",
                            profileName = "David Andrew",
                            notificationCount = 5,
                        )
                    }

                    item {
                        SectionLabel(
                            text = "Assigned Trucks",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }

                    items(items = state.issues, key = { it.id }) { issue ->
                        HomeVehicleTaskCard(
                            imageUrl = "", 
                            title = "Issue #${issue.id}",
                            subtitle = issue.description,
                            dateText = issue.created_at ?: "Unknown Date",
                            pendingTasksText = issue.status ?: "Pending",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = { currentScreen = HomeScreenDestination.VehicleInformation },
                        )
                    }

                    if (state.isLoading) {
                        item {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
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

                    item {
                        SectionLabel(
                            text = "Pending Tasks",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
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

            HomeScreenDestination.Tasks -> {
                TasksScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }

            HomeScreenDestination.Settings -> {
                SettingsScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
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
