package com.msn.valentinesgarage.activities.authenticationActivity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.LoginRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val token: String? = null,
    val userId: Int? = null,
    val role: String? = null,
    val error: String? = null,
)

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            try {
                val response = RetrofitClient.api.loginUser(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    _uiState.value = LoginUiState(
                        token = body.token,
                        userId = body.user_id,
                        role = body.role,
                    )
                } else {
                    _uiState.value = LoginUiState(error = "Invalid email or password")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Could not connect to server")
            }
        }
    }
}
