package com.msn.valentinesgarage.screens.mechanic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.CreateIssueRequest
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MechanicVehiclesUiState(
    val visits: List<MechanicVisit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val issueCreated: Boolean = false,
    val visitCompleted: Boolean = false
)

class MechanicVehiclesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MechanicVehiclesUiState())
    val uiState: StateFlow<MechanicVehiclesUiState> = _uiState

    fun loadAssignedVehicles(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, visitCompleted = false) }
            try {
                val response = RetrofitClient.api.getActiveVisits("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.update { it.copy(visits = response.body().orEmpty(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load vehicles") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun reportIssue(token: String, visitId: Int, description: String, cost: Double? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, issueCreated = false) }
            try {
                val res = RetrofitClient.api.createIssue(
                    "Bearer $token",
                    CreateIssueRequest(visitId, description, cost = cost)
                )
                if (res.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, issueCreated = true) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to report issue") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun completeVisit(token: String, visitId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.completeVisit("Bearer $token", visitId)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, visitCompleted = true) }
                    loadAssignedVehicles(token)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null && errorBody.contains("message")) {
                        // Simple way to extract message from JSON like {"message":"..."}
                        errorBody.substringAfter("\"message\":\"").substringBefore("\"")
                    } else {
                        "Failed to complete visit"
                    }
                    _uiState.update { it.copy(isLoading = false, error = errorMessage) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun resetIssueCreated() {
        _uiState.update { it.copy(issueCreated = false) }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }
}
