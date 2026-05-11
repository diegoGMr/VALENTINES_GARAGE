package com.msn.valentinesgarage.screens.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.LoginRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import com.msn.valentinesgarage.screens.dialog.DialogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailTouched: Boolean = false,
    val isPasswordTouched: Boolean = false,
    val hasSubmitted: Boolean = false,
    val isLoading: Boolean = false,
    val token: String? = null,
    val userId: Int? = null,
    val role: String? = null,
    val error: String? = null,
    val activeDialog: DialogType? = null,
)

class LoginViewModel : ViewModel() {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.update {
            it.copy(
                email = email.trim(),
                error = null,
            )
        }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update {
            it.copy(
                password = password,
                error = null,
            )
        }
    }

    fun onEmailFocusChanged(isFocused: Boolean) {
        if (!isFocused) {
            _uiState.update { it.copy(isEmailTouched = true) }
        }
    }

    fun onPasswordFocusChanged(isFocused: Boolean) {
        if (!isFocused) {
            _uiState.update { it.copy(isPasswordTouched = true) }
        }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(activeDialog = null) }
    }

    private fun emailErrorFor(email: String): String? {
        if (email.isBlank()) return "Email is required"
        if (!emailRegex.matches(email)) return "Invalid email format"
        return null
    }

    private fun passwordErrorFor(password: String): String? {
        if (password.isBlank()) return "Password is required"
        return null
    }

    fun login() {
        val currentState = _uiState.value
        val emailError = emailErrorFor(currentState.email)
        val passwordError = passwordErrorFor(currentState.password)

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    hasSubmitted = true,
                    error = emailError ?: passwordError,
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    hasSubmitted = true,
                    isLoading = true,
                    error = null,
                    activeDialog = DialogType.Loading,
                )
            }

            try {
                val response = RetrofitClient.api.loginUser(
                    LoginRequest(currentState.email.trim(), currentState.password)
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                token = body.token,
                                userId = body.user_id,
                                role = body.role,
                                error = null,
                                activeDialog = null,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Login failed. Empty server response.",
                                activeDialog = DialogType.Error(
                                    title = "Login Failed",
                                    message = "Empty server response. Please try again.",
                                ),
                            )
                        }
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Invalid email or password",
                            activeDialog = DialogType.Error(
                                title = "Login Failed",
                                message = "Invalid email or password",
                            ),
                        )
                    }
                }
            } catch (_: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Could not connect to server",
                        activeDialog = DialogType.Error(
                            title = "Connection Error",
                            message = "Could not connect to server",
                        ),
                    )
                }
            }
        }
    }

    fun emailError(): String? {
        val state = _uiState.value
        if (!state.isEmailTouched && !state.hasSubmitted) return null
        return emailErrorFor(state.email)
    }

    fun passwordError(): String? {
        val state = _uiState.value
        if (!state.isPasswordTouched && !state.hasSubmitted) return null
        return passwordErrorFor(state.password)
    }

    fun isFormValid(): Boolean {
        val state = _uiState.value
        return emailErrorFor(state.email) == null && passwordErrorFor(state.password) == null
    }

    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
