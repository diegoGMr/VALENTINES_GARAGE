package com.msn.valentinesgarage.screens.mechanic.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.CreateIssueRequest
import com.msn.valentinesgarage.data.models.Issue
import com.msn.valentinesgarage.data.models.ResolveIssueRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class IssuesUiState(
    val issues: List<Issue> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

class IssuesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(IssuesUiState())
    val uiState: StateFlow<IssuesUiState> = _uiState

    fun loadIssues(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.getIssues("Bearer $token")
                if (response.isSuccessful) {
                    _uiState.update { it.copy(issues = response.body().orEmpty(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load issues") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun createIssue(token: String, visitId: Int, description: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = RetrofitClient.api.createIssue(
                    "Bearer $token",
                    CreateIssueRequest(visitId, description)
                )
                if (res.isSuccessful) {
                    loadIssues(token)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to create issue") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun resolveIssue(token: String, issueId: Int, notes: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = RetrofitClient.api.resolveIssue(
                    "Bearer $token", issueId, ResolveIssueRequest(notes?.ifBlank { null })
                )
                if (res.isSuccessful) {
                    loadIssues(token)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to resolve issue") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }
}
