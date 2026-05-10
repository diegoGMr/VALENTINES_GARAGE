package com.msn.valentinesgarage.screens.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.MechanicVisit
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientProgressUiState(
    val visits: List<MechanicVisit> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ClientProgressViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ClientProgressUiState())
    val uiState: StateFlow<ClientProgressUiState> = _uiState

    fun loadProgress(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.getClientProgress("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.update { it.copy(visits = response.body().orEmpty(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load progress") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
