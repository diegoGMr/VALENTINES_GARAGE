package com.msn.valentinesgarage.activities.authenticationActivity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.RegisterRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SignUpUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null,
)

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _uiState.value = SignUpUiState(error = "Passwords do not match")
            return
        }
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = SignUpUiState(error = "All fields are required")
            return
        }
        viewModelScope.launch {
            _uiState.value = SignUpUiState(isLoading = true)
            try {
                val response = RetrofitClient.api.registerUser(
                    RegisterRequest(
                        name = name,
                        email = email,
                        password = password,
                        phone = null,
                        role = "mechanic",
                    )
                )
                if (response.isSuccessful) {
                    _uiState.value = SignUpUiState(success = true)
                } else {
                    _uiState.value = SignUpUiState(error = "Registration failed. Email may already be in use.")
                }
            } catch (e: Exception) {
                _uiState.value = SignUpUiState(error = "Could not connect to server")
            }
        }
    }

    fun resetState() {
        _uiState.value = SignUpUiState()
    }
}
