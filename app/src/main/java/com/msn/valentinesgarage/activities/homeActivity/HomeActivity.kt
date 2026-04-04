package com.msn.valentinesgarage.activities.homeActivity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.msn.valentinesgarage.R
import com.msn.valentinesgarage.activities.homeActivity.composables.HomeHeaderBanner
import com.msn.valentinesgarage.activities.homeActivity.composables.HomeVehicleTaskCard
import com.msn.valentinesgarage.activities.homeActivity.composables.SectionLabel
import com.msn.valentinesgarage.activities.settingsActivity.SettingsActivity
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Cog
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.Tasks
import compose.icons.fontawesomeicons.solid.User

enum class HomeBottomTab(
    val title: String,
    val icon: ImageVector,
) {
    Home(title = "Home", icon = FontAwesomeIcons.Solid.Home),
    Tasks(title = "Tasks", icon = FontAwesomeIcons.Solid.Tasks),
    Profile(title = "Profile", icon = FontAwesomeIcons.Solid.User),
    Settings(title = "Settings", icon = FontAwesomeIcons.Solid.Cog),
}

private enum class HomeScreen {
    Dashboard,
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

@Composable
fun HomeActivity(
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableStateOf(HomeBottomTab.Home) }
    var currentScreen by remember { mutableStateOf(HomeScreen.Dashboard) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.White,
        bottomBar = {
            NavigationBar(containerColor = AppColors.White) {
                HomeBottomTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = {
                            selectedTab = tab
                            currentScreen = when (tab) {
                                HomeBottomTab.Home -> HomeScreen.Dashboard
                                HomeBottomTab.Settings -> HomeScreen.Settings
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
            HomeScreen.Dashboard -> {
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

                    items(items = sampleVehicleCards, key = { it.id }) { vehicle ->
                        HomeVehicleTaskCard(
                            imageUrl = vehicle.imageUrl,
                            title = vehicle.title,
                            subtitle = vehicle.subtitle,
                            dateText = vehicle.dateText,
                            pendingTasksText = vehicle.pendingTasksText,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            onClick = { currentScreen = HomeScreen.VehicleInformation },
                        )
                    }

                    item {
                        SectionLabel(
                            text = "Pending Tasks",
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }
            }

            HomeScreen.VehicleInformation -> {
                VehicleInformationActivity(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }

            HomeScreen.Settings -> {
                SettingsActivity(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = innerPadding.calculateBottomPadding()),
                )
            }
        }
    }
}

private val sampleVehicleCards = listOf(
    VehicleCardUi(
        id = "1",
        imageUrl = "https://i.pinimg.com/1200x/5b/15/f2/5b15f2cc8df52cf83c65e0cb79da6468.jpg",
        title = "HSD343",
        subtitle = "Scania Railer",
        dateText = "18 March 2026 12:07am",
        pendingTasksText = "19 pending tasks",
    ),
    VehicleCardUi(
        id = "2",
        imageUrl = "https://i.pinimg.com/1200x/ad/a5/25/ada52531f654fd58bbd43fa4ca0483fd.jpg",
        title = "JKL918",
        subtitle = "Volvo FH16",
        dateText = "18 March 2026 10:22am",
        pendingTasksText = "7 pending tasks",
    ),
    VehicleCardUi(
        id = "3",
        imageUrl = "https://i.pinimg.com/1200x/ad/a5/25/ada52531f654fd58bbd43fa4ca0483fd.jpg",
        title = "QTR552",
        subtitle = "MAN TGX",
        dateText = "17 March 2026 08:42pm",
        pendingTasksText = "3 pending tasks",
    ),
)

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeActivityPreview() {
    HomeActivity()
}
