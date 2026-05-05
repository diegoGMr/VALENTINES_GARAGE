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
    val bookings: BookingSlotsResponse? = null,
    val issues: List<Issue> = emptyList(),
    val tasks: List<Task> = emptyList(),
    val adminUsers: List<AdminUserRead> = emptyList(),
    val isLoading: Boolean = false,
    val infoMessage: String? = null,
    val error: String? = null,
)

class HomeViewModel : ViewModel() {
    private val _state = MutableStateFlow(HomeDataState())
    val state: StateFlow<HomeDataState> = _state

    fun loadData(token: String, role: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null, infoMessage = null)
            val bearer = "Bearer $token"
            try {
                val bookingRes = RetrofitClient.api.getBookingSlots(bearer, LocalDate.now().toString())
                val bookings = if (bookingRes.isSuccessful) bookingRes.body() else null

                val issueRes = RetrofitClient.api.getIssues(bearer)
                val issues = if (issueRes.isSuccessful) issueRes.body().orEmpty() else emptyList()

                val taskRes = RetrofitClient.api.getTasks(bearer)
                val tasks = if (taskRes.isSuccessful) taskRes.body().orEmpty() else emptyList()

                val adminUsers = if (role == "admin") {
                    val usersRes = RetrofitClient.api.getAdminUsers(bearer)
                    if (usersRes.isSuccessful) usersRes.body().orEmpty() else emptyList()
                } else {
                    emptyList()
                }

                _state.value = _state.value.copy(
                    bookings = bookings,
                    issues = issues,
                    tasks = tasks,
                    adminUsers = adminUsers,
                    isLoading = false,
                )
            } catch (_: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = "Failed to load server data")
            }
        }
    }

    fun createIssue(token: String, title: String, description: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.createIssue("Bearer $token", CreateIssueRequest(title, description))
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

    fun createTask(token: String, issueId: Int, title: String, description: String, category: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.createTask(
                    "Bearer $token",
                    CreateTaskRequest(issueId, title, description, category),
                )
                if (res.isSuccessful) {
                    _state.value = _state.value.copy(infoMessage = "Task created")
                } else {
                    _state.value = _state.value.copy(error = "Task creation not allowed")
                }
            } catch (_: Exception) {
                _state.value = _state.value.copy(error = "Task request failed")
            }
        }
    }

    fun createBooking(token: String, customerName: String) {
        viewModelScope.launch {
            try {
                val res = RetrofitClient.api.createBookingSlot(
                    "Bearer $token",
                    CreateBookingRequest(LocalDate.now().toString(), customerName),
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
}
