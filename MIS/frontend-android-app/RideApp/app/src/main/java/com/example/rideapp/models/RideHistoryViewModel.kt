package com.example.rideapp.models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.State

class RideHistoryViewModel : ViewModel() {
    private val _rideHistory = mutableStateOf<List<RideHistorySummary>>(emptyList())
    val rideHistory: State<List<RideHistorySummary>> get() = _rideHistory

    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> get() = _isLoading

    fun fetchRideHistory() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                delay(2000)

                // Sample data
                val response = listOf(
                    mapOf("id" to 2, "userId" to "8bUjtFOKcfdrW9SGluag8dG3Si53", "vehicleType" to "Bike", "distance" to "23.17 km", "fare" to "579.15"),
                    mapOf("id" to 4, "userId" to "8bUjtFOKcfdrW9SGluag8dG3Si53", "vehicleType" to "Bike", "distance" to "23.17 km", "fare" to "579.15")
                )

                // Map response to RideHistorySummary
                _rideHistory.value = response.map { data ->
                    RideHistorySummary(
                        id = data["id"] as Int,
                        userId = data["userId"] as String,
                        vehicleType = data["vehicleType"] as String,
                        distance = data["distance"] as String,
                        fare = data["fare"] as String
                    )
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}