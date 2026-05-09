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
    val dbStats: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isCreatingUser: Boolean = false,
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

                    val statsRes = statsDeferred.await()
                    val workloadRes = workloadDeferred.await()
                    val dbStatsRes = dbStatsDeferred.await()
                    val usersRes = usersDeferred.await()

                    if (statsRes.isSuccessful || workloadRes.isSuccessful || dbStatsRes.isSuccessful || usersRes.isSuccessful) {
                        _uiState.update {
                            it.copy(
                                stats = if (statsRes.isSuccessful) statsRes.body() else it.stats,
                                mechanics = if (workloadRes.isSuccessful) workloadRes.body().orEmpty() else it.mechanics,
                                dbStats = if (dbStatsRes.isSuccessful) dbStatsRes.body().orEmpty() else it.dbStats,
                                users = if (usersRes.isSuccessful) usersRes.body().orEmpty() else it.users,
                                isLoading = false,
                                error = if (!statsRes.isSuccessful || !workloadRes.isSuccessful || !dbStatsRes.isSuccessful || !usersRes.isSuccessful) 
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
}
