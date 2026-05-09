package com.msn.valentinesgarage.screens.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.Booking
import com.msn.valentinesgarage.data.models.CreateBookingRequest
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class BookingUiState(
    val bookings: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val bookingSuccess: Boolean = false,
)

class BookingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState

    fun loadBookings(token: String, date: String = LocalDate.now().toString()) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.getBookings("Bearer $token", date)
                if (response.isSuccessful) {
                    _uiState.update { it.copy(bookings = response.body().orEmpty(), isLoading = false) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load bookings") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun createBooking(token: String, clientId: Int, truckId: Int, date: String, time: String, clientNotes: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val response = RetrofitClient.api.createBooking(
                    "Bearer $token",
                    CreateBookingRequest(clientId, truckId, date, time, clientNotes)
                )
                if (response.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, bookingSuccess = true) }
                    loadBookings(token, date)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to create booking") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(bookingSuccess = false) }
    }
}
