package com.msn.valentinesgarage.screens.mechanic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MechanicServiceHistoryUiState(
    val visits: List<MechanicVisit> = emptyList(),
    val selectedPlate: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

class MechanicServiceHistoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MechanicServiceHistoryUiState())
    val uiState: StateFlow<MechanicServiceHistoryUiState> = _uiState

    fun loadHistory(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.getMechanicServiceHistory("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            visits = response.body().orEmpty(),
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load service history") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun selectPlate(plate: String?) {
        _uiState.update { it.copy(selectedPlate = plate) }
    }
}

