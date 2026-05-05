package com.msn.valentinesgarage.activities.homeActivity

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
import com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeViewModel
import com.msn.valentinesgarage.activities.settingsActivity.SettingsActivity
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

@Composable
fun HomeActivity(
    modifier: Modifier = Modifier,
    token: String = "",
    role: String = "mechanic",
    onLogout: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
) {
    var selectedTab by remember { mutableStateOf(HomeBottomTab.Home) }
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
                        onClick = { selectedTab = tab },
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
        when (selectedTab) {
            HomeBottomTab.Home -> RoleHomePage(role = role, state = state, modifier = Modifier.padding(innerPadding))
            HomeBottomTab.Booking -> BookingPage(
                role = role,
                state = state,
                onCreate = { customer -> homeViewModel.createBooking(token, customer) },
                onRefresh = { homeViewModel.loadData(token, role) },
                modifier = Modifier.padding(innerPadding),
            )
            HomeBottomTab.Issues -> IssuesPage(
                role = role,
                state = state,
                onCreate = { title, desc -> homeViewModel.createIssue(token, title, desc) },
                onRefresh = { homeViewModel.loadData(token, role) },
                modifier = Modifier.padding(innerPadding),
            )
            HomeBottomTab.Tasks -> TasksPage(
                role = role,
                state = state,
                onCreate = { issueId, title, desc, category ->
                    homeViewModel.createTask(token, issueId, title, desc, category)
                },
                onRefresh = { homeViewModel.loadData(token, role) },
                modifier = Modifier.padding(innerPadding),
            )
            HomeBottomTab.Admin -> AdminPage(state = state, modifier = Modifier.padding(innerPadding))
            HomeBottomTab.Settings, HomeBottomTab.Profile -> SettingsActivity(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
private fun RoleHomePage(role: String, state: com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeDataState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Logged in as: $role")
        Text("Use tabs below. UI and backend permissions are role-based.")
        state.infoMessage?.let { Text(it, color = AppColors.Orange) }
        state.error?.let { Text(it, color = AppColors.Red) }
    }
}

@Composable
private fun BookingPage(
    role: String,
    state: com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeDataState,
    onCreate: (String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var customerName by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Bookings (max 3 slots/day)")
        Text("Used: ${state.bookings?.usedSlots ?: 0} / 3, Remaining: ${state.bookings?.remainingSlots ?: 3}")
        OutlinedTextField(value = customerName, onValueChange = { customerName = it }, label = { Text("Customer name") })
        Button(onClick = { if (customerName.isNotBlank()) onCreate(customerName) }) { Text("Create booking") }
        Button(onClick = onRefresh) { Text("Refresh") }
        Text("Bookings for today:")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.bookings?.bookings.orEmpty()) { booking -> Text("- ${booking.customer_name}") }
        }
        if (role == "admin") Text("Admin can read all booking records from backend endpoints.")
    }
}

@Composable
private fun IssuesPage(
    role: String,
    state: com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeDataState,
    onCreate: (String, String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Issues")
        if (role == "inspector") {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Issue title") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            Button(onClick = { if (title.isNotBlank() && description.isNotBlank()) onCreate(title, description) }) {
                Text("Create issue")
            }
        } else {
            Text("You can view issues only.")
        }
        Button(onClick = onRefresh) { Text("Refresh") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.issues) { issue -> Text("- ${issue.title} (${issue.status})") }
        }
    }
}

@Composable
private fun TasksPage(
    role: String,
    state: com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeDataState,
    onCreate: (Int, String, String, String) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var issueId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("normal_mechanic") }
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Tasks")
        if (role == "lead_mechanic") {
            OutlinedTextField(value = issueId, onValueChange = { issueId = it }, label = { Text("Issue ID") })
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Task title") })
            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (inspector/lead_mechanic/normal_mechanic)") })
            Button(onClick = {
                val parsedIssueId = issueId.toIntOrNull()
                if (parsedIssueId != null && title.isNotBlank() && description.isNotBlank()) {
                    onCreate(parsedIssueId, title, description, category)
                }
            }) { Text("Create task") }
        } else {
            Text("You can view tasks only.")
        }
        Button(onClick = onRefresh) { Text("Refresh") }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.tasks) { task -> Text("- ${task.title} [${task.assigned_category}]") }
        }
    }
}

@Composable
private fun AdminPage(
    state: com.msn.valentinesgarage.activities.homeActivity.viewmodels.HomeDataState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Admin Read: Users")
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.adminUsers) { user -> Text("- ${user.full_name} (${user.role})") }
        }
    }
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun HomeActivityPreview() {
    HomeActivity()
}
