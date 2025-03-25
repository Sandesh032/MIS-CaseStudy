package com.example.rideapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RideSharingViewModel : ViewModel() {
    private val _distance = MutableStateFlow(0.0)  // Distance in km
    val distance: StateFlow<Double> = _distance

    private val _estimatedFares = MutableStateFlow<Map<String, Int>>(emptyMap())
    val estimatedFares: StateFlow<Map<String, Int>> = _estimatedFares

    private val pricePerKm = mapOf(
        "Bike" to 5,       // ₹5 per km
        "Car" to 10,       // ₹10 per km
        "Shuttle" to 7,    // ₹7 per km
        "E-Rickshaw" to 6  // ₹6 per km
    )

    fun fetchDistance(pickup: String, drop: String) {
        viewModelScope.launch {
            try {
                // 🚀 Call Google Distance Matrix API (Mocked Response for now)
                val mockDistance = 8.5  // Assume 8.5 km for now
                _distance.value = mockDistance

                val fares = pricePerKm.mapValues { (_, rate) -> (mockDistance * rate).toInt() }
                _estimatedFares.value = fares

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun bookRide(rideMode: String) {
        viewModelScope.launch {
            // 🚀 Implement backend API call for ride booking
            println("Booking Ride in $rideMode mode. Distance: ${_distance.value} km")
        }
    }
}