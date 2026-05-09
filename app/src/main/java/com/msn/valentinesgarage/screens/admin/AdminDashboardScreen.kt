package com.msn.valentinesgarage.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.msn.valentinesgarage.data.models.AdminUserRead
import com.msn.valentinesgarage.data.models.MechanicWorkloadResponse
import com.msn.valentinesgarage.data.models.RegisterRequest
import com.msn.valentinesgarage.screens.admin.viewmodels.AdminDashboardViewModel
import com.msn.valentinesgarage.screens.dialog.FullLoadingScreen
import com.msn.valentinesgarage.screens.home.composables.SectionLabel
import com.msn.valentinesgarage.theme.AppColors
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.*

// UI model for a stat card on the admin overview
data class AdminStatUi(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier,
    token: String = "",
    viewModel: AdminDashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateUserDialog by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadData(token)
        }
    }

    if (uiState.isLoading && uiState.stats == null) {
        FullLoadingScreen(message = "Loading Dashboard...")
        return
    }

    val stats = listOf(
        AdminStatUi(
            "Total Trucks",
            maxOf(uiState.stats?.totalTrucks ?: 0, uiState.dbStats["trucks"] ?: 0).toString(),
            FontAwesomeIcons.Solid.Truck,
            AppColors.Orange
        ),
        AdminStatUi(
            "Mechanics",
            maxOf(uiState.stats?.totalMechanics ?: 0, uiState.mechanics.size).toString(),
            FontAwesomeIcons.Solid.User,
            AppColors.Green
        ),
        AdminStatUi(
            "Open Issues",
            maxOf(uiState.stats?.openIssues ?: 0, uiState.mechanics.sumOf { it.openTasks }).toString(),
            FontAwesomeIcons.Solid.ExclamationTriangle,
            AppColors.Red
        ),
        AdminStatUi(
            "Total Appointments",
            maxOf(uiState.stats?.todayAppointments ?: 0, uiState.dbStats["booking_slots"] ?: 0).toString(),
            FontAwesomeIcons.Solid.Calendar,
            AppColors.Pink
        ),
    )

    PullToRefreshBox(
        isRefreshing = uiState.isLoading,
        onRefresh = { viewModel.loadData(token) },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
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

            uiState.error?.let { error ->
                item {
                    Text(text = error, color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            }

            // Stats grid
            item(key = "stats") {
                SectionLabel(
                    text = "Overview",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
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

            // User Management Section
            item(key = "users_header") {
                SectionLabel(
                    text = "User Management",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            items(uiState.users, key = { it.id }) { user ->
                AdminUserRow(
                    user = user,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item(key = "add_user_action") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Surface(
                        onClick = { showCreateUserDialog = true },
                        color = AppColors.Orange,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = FontAwesomeIcons.Solid.Plus,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                "ADD NEW USER",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Mechanics workload
            item(key = "mechanics_header") {
                SectionLabel(
                    text = "Mechanics Workload",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            items(uiState.mechanics, key = { it.id }) { mechanic ->
                MechanicWorkloadRow(
                    mechanic = mechanic,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            // Database stats
            item(key = "db_stats") {
                SectionLabel(
                    text = "Database Statistics",
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                        uiState.dbStats.forEach { (table, count) ->
                            DbStatRow(label = "Total ${table.replaceFirstChar { it.uppercase() }}", value = count.toString())
                        }
                    }
                }
            }
        }

        if (showCreateUserDialog) {
            CreateUserDialog(
                onDismiss = { showCreateUserDialog = false },
                onCreate = { req: RegisterRequest ->
                    viewModel.createUser(token, req) {
                        showCreateUserDialog = false
                    }
                }
            )
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
private fun AdminUserRow(
    user: AdminUserRead,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(AppColors.Orange.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(FontAwesomeIcons.Solid.User, contentDescription = null, tint = AppColors.Orange, modifier = Modifier.size(18.dp))
            }
            Column {
                Text(user.full_name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AppColors.FontBlackStrong)
                Text(user.email, fontSize = 12.sp, color = AppColors.TextHint)
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = when(user.role) {
                    "admin" -> AppColors.Red.copy(alpha = 0.1f)
                    "mechanic" -> AppColors.Green.copy(alpha = 0.1f)
                    else -> AppColors.LightGray.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = user.role.uppercase(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = when(user.role) {
                        "admin" -> AppColors.Red
                        "mechanic" -> AppColors.Green
                        else -> AppColors.FontBlackMedium
                    }
                )
            }
        }
    }
}

@Composable
private fun MechanicWorkloadRow(
    mechanic: MechanicWorkloadResponse,
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
                        imageVector = FontAwesomeIcons.Solid.ExclamationTriangle,
                        contentDescription = null,
                        tint = AppColors.Orange,
                        modifier = Modifier.size(10.dp),
                    )
                    Text(
                        text = "${mechanic.openTasks} open issues",
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
                        text = "${mechanic.completedToday} completed",
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateUserDialog(
    onDismiss: () -> Unit,
    onCreate: (RegisterRequest) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("mechanic") }
    val roles = listOf("admin", "mechanic", "client")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") })
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") })
                
                Text("Role", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    roles.forEach { r ->
                        FilterChip(
                            selected = role == r,
                            onClick = { role = r },
                            label = { Text(r.uppercase()) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(RegisterRequest(name, email, password, if(phone.isBlank()) null else phone, role)) },
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.Orange)
            ) {
                Text("CREATE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL") }
        }
    )
}

@Preview(showBackground = true, widthDp = 375, heightDp = 812)
@Composable
private fun AdminDashboardScreenPreview() {
    AdminDashboardScreen()
}
