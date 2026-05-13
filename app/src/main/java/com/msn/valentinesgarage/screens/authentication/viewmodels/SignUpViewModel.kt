package com.msn.valentinesgarage.screens.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.RegisterRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import com.msn.valentinesgarage.screens.dialog.DialogType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val agreedToTerms: Boolean = false,
    val isFullNameTouched: Boolean = false,
    val isEmailTouched: Boolean = false,
    val isPhoneTouched: Boolean = false,
    val isPasswordTouched: Boolean = false,
    val isConfirmPasswordTouched: Boolean = false,
    val hasSubmitted: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val token: String? = null,
    val userId: Int? = null,
    val role: String? = null,
    val error: String? = null,
    val activeDialog: DialogType? = null,
)

class SignUpViewModel : ViewModel() {

    private val emailRegex = Regex("^[A-Za-z0-9._%+-]{3,}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val fullNameRegex = Regex("^[A-Za-z]+([ '-][A-Za-z]+)*$")
    private val hasUppercase = Regex(".*[A-Z].*")
    private val hasNumber = Regex(".*[0-9].*")
    private val hasSymbol = Regex(".*[^A-Za-z0-9].*")

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState

    fun onFullNameChanged(value: String) {
        _uiState.update { it.copy(fullName = value.trimStart(), error = null) }
    }

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value.trim(), error = null) }
    }

    fun onPhoneChanged(value: String) {
        val digits = value.filter { it.isDigit() }.take(10)
        _uiState.update { it.copy(phone = digits, error = null) }
    }

    fun onPhoneFocusChanged(isFocused: Boolean) {
        if (!isFocused) _uiState.update { it.copy(isPhoneTouched = true) }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value, error = null) }
    }

    fun onAgreedToTermsChanged(agreed: Boolean) {
        _uiState.update { it.copy(agreedToTerms = agreed, error = null) }
    }

    fun onFullNameFocusChanged(isFocused: Boolean) {
        if (!isFocused) _uiState.update { it.copy(isFullNameTouched = true) }
    }

    fun onEmailFocusChanged(isFocused: Boolean) {
        if (!isFocused) _uiState.update { it.copy(isEmailTouched = true) }
    }

    fun onPasswordFocusChanged(isFocused: Boolean) {
        if (!isFocused) _uiState.update { it.copy(isPasswordTouched = true) }
    }

    fun onConfirmPasswordFocusChanged(isFocused: Boolean) {
        if (!isFocused) _uiState.update { it.copy(isConfirmPasswordTouched = true) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(activeDialog = null) }
    }

    private fun fullNameErrorFor(fullName: String): String? {
        if (fullName.isBlank()) return "Full name is required"
        if (fullName.contains("@")) return "Invalid name format"
        if (!fullNameRegex.matches(fullName.trim())) return "Invalid name format"
        return null
    }

    private fun phoneErrorFor(phone: String): String? {
        if (phone.isBlank()) return "Phone number is required"
        if (phone.length != 10) return "Phone number must be exactly 10 digits"
        return null
    }

    private fun emailErrorFor(email: String): String? {
        if (email.isBlank()) return "Email is required"
        if (!emailRegex.matches(email.trim())) return "Invalid email format"
        return null
    }

    private fun passwordErrorFor(password: String): String? {
        if (password.isBlank()) return "Password is required"
        if (password.length < 4) return "Password must be at least 4 characters"
        return null
    }

    private fun confirmPasswordErrorFor(password: String, confirmPassword: String): String? {
        if (confirmPassword.isBlank()) return "Confirm password is required"
        if (password != confirmPassword) return "Passwords do not match"
        return null
    }

    fun register() {
        val state = _uiState.value
        val nameError = fullNameErrorFor(state.fullName)
        val emailError = emailErrorFor(state.email)
        val phoneError = phoneErrorFor(state.phone)
        val passwordError = passwordErrorFor(state.password)
        val confirmError = confirmPasswordErrorFor(state.password, state.confirmPassword)
        val termsError = if (!state.agreedToTerms) "Please agree to terms and conditions" else null

        if (nameError != null || emailError != null || phoneError != null || passwordError != null || confirmError != null || termsError != null) {
            _uiState.update {
                it.copy(
                    hasSubmitted = true,
                    error = nameError ?: emailError ?: phoneError ?: passwordError ?: confirmError ?: termsError,
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
                val response = RetrofitClient.api.registerUser(
                    RegisterRequest(
                        name = state.fullName.trim(),
                        email = state.email.trim(),
                        password = state.password,
                        phone = state.phone,
                        role = "client",
                    )
                )
                if (response.isSuccessful) {
                    val body = response.body()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            success = true,
                            token = body?.token,
                            userId = body?.userId,
                            role = body?.role,
                            activeDialog = DialogType.Success(
                                title = "Account Created",
                                message = "Your account was created successfully.",
                            ),
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Registration failed. Email may already be in use.",
                            activeDialog = DialogType.Error(
                                title = "Sign Up Failed",
                                message = "Registration failed. Email may already be in use.",
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

    fun resetState() {
        _uiState.value = SignUpUiState(agreedToTerms = _uiState.value.agreedToTerms)
    }

    fun fullNameError(): String? {
        val state = _uiState.value
        if (!state.isFullNameTouched && !state.hasSubmitted) return null
        return fullNameErrorFor(state.fullName)
    }

    fun emailError(): String? {
        val state = _uiState.value
        if (!state.isEmailTouched && !state.hasSubmitted) return null
        return emailErrorFor(state.email)
    }

    fun phoneError(): String? {
        val state = _uiState.value
        if (!state.isPhoneTouched && !state.hasSubmitted) return null
        return phoneErrorFor(state.phone)
    }

    fun passwordError(): String? {
        val state = _uiState.value
        if (!state.isPasswordTouched && !state.hasSubmitted) return null
        return passwordErrorFor(state.password)
    }

    fun confirmPasswordError(): String? {
        val state = _uiState.value
        if (!state.isConfirmPasswordTouched && !state.hasSubmitted) return null
        return confirmPasswordErrorFor(state.password, state.confirmPassword)
    }

    fun isFormValid(): Boolean {
        val state = _uiState.value
        return fullNameErrorFor(state.fullName) == null &&
                emailErrorFor(state.email) == null &&
                phoneErrorFor(state.phone) == null &&
                passwordErrorFor(state.password) == null &&
                confirmPasswordErrorFor(state.password, state.confirmPassword) == null &&
                state.agreedToTerms
    }
}
