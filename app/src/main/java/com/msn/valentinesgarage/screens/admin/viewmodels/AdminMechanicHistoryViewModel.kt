package com.msn.valentinesgarage.screens.admin.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.AdminMechanicHistoryVisit
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminMechanicHistoryUiState(
    val visits: List<AdminMechanicHistoryVisit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class AdminMechanicHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminMechanicHistoryUiState())
    val uiState: StateFlow<AdminMechanicHistoryUiState> = _uiState

    fun loadHistory(token: String) {
        if (token.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Missing token") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val response = RetrofitClient.api.getAdminMechanicHistory("Bearer $token")
                if (response.isSuccessful) {
                    val visits = response.body().orEmpty().sortedByDescending { it.visitId }
                    _uiState.update {
                        it.copy(
                            visits = visits,
                            isLoading = false,
                            error = null,
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load mechanic history",
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error",
                    )
                }
            }
        }
    }
}

