package com.msn.valentinesgarage.screens.client.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.msn.valentinesgarage.data.models.RegisterTruckRequest
import com.msn.valentinesgarage.data.models.Truck
import com.msn.valentinesgarage.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VehicleUiState(
    val myTrucks: List<Truck> = emptyList(),
    val specialities: List<Pair<Int, String>> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false,
)

class VehicleViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState

    fun loadData(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val bearer = "Bearer $token"
                val trucksRes = RetrofitClient.api.getUserTrucks(bearer)
                val specsRes = RetrofitClient.api.getSpecialities(bearer)

                if (trucksRes.isSuccessful && specsRes.isSuccessful) {
                    val specs = specsRes.body()?.mapNotNull {
                        val id = (it["id"] as? Double)?.toInt() ?: (it["id"] as? Int)
                        val name = it["name"] as? String
                        if (id != null && name != null) id to name else null
                    } ?: emptyList()

                    _uiState.update { it.copy(
                        myTrucks = trucksRes.body().orEmpty(),
                        specialities = specs,
                        isLoading = false
                    ) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load vehicle data") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun registerVehicle(token: String, userId: Int, plate: String, specialityId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val res = RetrofitClient.api.registerTruck(
                    "Bearer $token",
                    RegisterTruckRequest(plate_number = plate, userId = userId, specialityId = specialityId)
                )
                if (res.isSuccessful) {
                    _uiState.update { it.copy(isLoading = false, registrationSuccess = true) }
                    loadData(token)
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Registration failed") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Unknown error") }
            }
        }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }
}
