package com.msn.valentinesgarage.screens.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.AdminStatsResponse
import com.msn.valentinesgarage.data.models.AdminUserRead
import com.msn.valentinesgarage.data.models.MechanicWorkloadResponse
import com.msn.valentinesgarage.data.models.RegisterRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminDashboardUiState(
    val stats: AdminStatsResponse? = null,
    val mechanics: List<MechanicWorkloadResponse> = emptyList(),
    val users: List<AdminUserRead> = emptyList(),
    val serviceFlows: List<AdminServiceFlowUi> = emptyList(),
    val dbStats: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingUser: Boolean = false,
)

data class AdminServiceFlowUi(
    val serviceId: Int,
    val bookingId: Int?,
    val mechanicName: String,
    val mechanicRole: String,
    val vehicleLabel: String,
    val clientName: String,
    val clientNotes: String?,
    val issues: List<AdminServiceIssueUi>,
)

data class AdminServiceIssueUi(
    val issueId: Int,
    val description: String,
    val resolved: Boolean,
    val resolutionNotes: String?,
    val cost: Double?,
)

class AdminDashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState

    fun loadData(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val bearer = "Bearer $token"
            try {
                coroutineScope {
                    val statsDeferred = async { RetrofitClient.api.getAdminStats(bearer) }
                    val workloadDeferred = async { RetrofitClient.api.getMechanicWorkload(bearer) }
                    val dbStatsDeferred = async { RetrofitClient.api.getDatabaseStats(bearer) }
                    val usersDeferred = async { RetrofitClient.api.getAdminUsers(bearer) }
                    val activeVisitsDeferred = async { RetrofitClient.api.getActiveVisits(bearer) }
                    val issuesDeferred = async { RetrofitClient.api.getIssues(bearer) }

                    val statsRes = statsDeferred.await()
                    val workloadRes = workloadDeferred.await()
                    val dbStatsRes = dbStatsDeferred.await()
                    val usersRes = usersDeferred.await()
                    val activeVisitsRes = activeVisitsDeferred.await()
                    val issuesRes = issuesDeferred.await()

                    val users = if (usersRes.isSuccessful) usersRes.body().orEmpty() else _uiState.value.users
                    val serviceFlows = buildServiceFlows(
                        visits = if (activeVisitsRes.isSuccessful) activeVisitsRes.body().orEmpty() else emptyList(),
                        issues = if (issuesRes.isSuccessful) issuesRes.body().orEmpty() else emptyList(),
                        users = users,
                    )

                    if (
                        statsRes.isSuccessful || workloadRes.isSuccessful || dbStatsRes.isSuccessful ||
                        usersRes.isSuccessful || activeVisitsRes.isSuccessful || issuesRes.isSuccessful
                    ) {
                        _uiState.update {
                            it.copy(
                                stats = if (statsRes.isSuccessful) statsRes.body() else it.stats,
                                mechanics = if (workloadRes.isSuccessful) workloadRes.body().orEmpty() else it.mechanics,
                                dbStats = if (dbStatsRes.isSuccessful) dbStatsRes.body().orEmpty() else it.dbStats,
                                users = users,
                                serviceFlows = serviceFlows,
                                isLoading = false,
                                error = if (
                                    !statsRes.isSuccessful || !workloadRes.isSuccessful || !dbStatsRes.isSuccessful ||
                                    !usersRes.isSuccessful || !activeVisitsRes.isSuccessful || !issuesRes.isSuccessful
                                )
                                    "Some data could not be loaded" else null
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Failed to load dashboard data") }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun createUser(token: String, request: RegisterRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isCreatingUser = true) }
            try {
                val res = RetrofitClient.api.registerUser(request)
                if (res.isSuccessful) {
                    onSuccess()
                    loadData(token)
                } else {
                    _uiState.update { it.copy(error = "Failed to create user") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            } finally {
                _uiState.update { it.copy(isCreatingUser = false) }
            }
        }
    }

    private fun buildServiceFlows(
        visits: List<com.msn.valentinesgarage.data.models.MechanicVisit>,
        issues: List<com.msn.valentinesgarage.data.models.Issue>,
        users: List<AdminUserRead>,
    ): List<AdminServiceFlowUi> {
        val userById = users.associateBy { it.id }
        val issuesByVisit = issues.groupBy { it.visitId }

        return visits.map { visit ->
            val mechanicId = visit.mechanicId ?: visit.assignedMechanics.firstOrNull()?.mechanicId
            val mechanic = mechanicId?.let { userById[it] }
            val visitIssues = issuesByVisit[visit.visitId].orEmpty()

            AdminServiceFlowUi(
                serviceId = visit.visitId,
                bookingId = visit.bookingId,
                mechanicName = mechanic?.full_name ?: "Mechanic #${mechanicId ?: "Unknown"}",
                mechanicRole = mechanic?.role ?: "mechanic",
                vehicleLabel = visit.truck?.plate_number ?: "Vehicle #${visit.truckId ?: "-"}",
                clientName = visit.client?.full_name ?: "Client #${visit.clientId ?: "-"}",
                clientNotes = visit.clientNotes,
                issues = visitIssues.map { issue ->
                    AdminServiceIssueUi(
                        issueId = issue.id,
                        description = issue.description,
                        resolved = issue.resolved == true,
                        resolutionNotes = issue.resolutionNotes,
                        cost = issue.cost,
                    )
                }.sortedWith(compareBy<AdminServiceIssueUi> { it.resolved }.thenBy { it.issueId })
            )
        }.sortedByDescending { it.serviceId }
    }
}
