package com.msn.valentinesgarage.activities.homeActivity.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.*
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeDataState(
    val user: User? = null,
    val bookings: List<Booking>? = null,
    val issues: List<Issue> = emptyList(),
    val adminUsers: List<AdminUserRead> = emptyList(),
    val isLoading: Boolean = false,
    val infoMessage: String? = null,
    val error: String? = null,
)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeDataState())
    val state: StateFlow<HomeDataState> = _state

    fun loadData(token: String, role: String, userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, infoMessage = null)
            val bearer = "Bearer $token"
            try {
                val userRes = RetrofitClient.api.getUserById(bearer, userId)
                val user = if (userRes.isSuccessful) userRes.body() else null

                val bookingRes = RetrofitClient.api.getBookings(bearer, LocalDate.now().toString())
                val bookings = if (bookingRes.isSuccessful) bookingRes.body() else null

                val issueRes = RetrofitClient.api.getIssues(bearer)
                val issues = if (issueRes.isSuccessful) issueRes.body().orEmpty() else emptyList()

                val adminUsers = if (role == "admin") {
                    val usersRes = RetrofitClient.api.getAdminUsers(bearer)
                    if (usersRes.isSuccessful) usersRes.body().orEmpty() else emptyList()
                } else {
                    emptyList()
                }

                _state.value = _state.value.copy(
                    user = user,
                    bookings = bookings,
                    issues = issues,
                    adminUsers = adminUsers,
                    isLoading = false,
                )
            } catch (_: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Failed to load server data")
            }
        }
    }

    fun createIssue(token: String, visitId: Int, description: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.createIssue("Bearer $token", CreateIssueRequest(visitId, description))
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(infoMessage = "Issue created")
                } else {
                    _state.value = _state.value.copy(error = "Issue creation not allowed")
                }
            } catch (_: Exception) {
                _state.value = _state.value.copy(error = "Issue request failed")
            }
        }
    }

    fun createBooking(token: String, clientId: Int, vehicleId: Int, date: String, time: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.createBooking(
                    "Bearer $token",
                    CreateBookingRequest(clientId, vehicleId, date, time),
                )
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(infoMessage = "Booking created")
                } else {
                    _state.value = _state.value.copy(error = "No available slots for today")
                }
            } catch (_: Exception) {
                _state.value = _state.value.copy(error = "Booking request failed")
            }
        }
    }

    fun assignToBooking(token: String, bookingId: Int, role: String, userId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            try {
                val res = RetrofitClient.api.assignMechanicToBooking("Bearer $token", bookingId)
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(infoMessage = "Assigned successfully. Visit created.")
                    loadData(token, role, userId)
                } else {
                    _state.value = _state.value.copy(isLoading = false, error = "Assignment failed")
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Error assigning booking")
            }
        }
    }
}
