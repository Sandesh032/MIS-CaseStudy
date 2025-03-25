package com.example.rideapp.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RideSharingViewModel : ViewModel() {
    private val _distance = MutableStateFlow(0.0)   // Distance in km
    val distance: StateFlow<Double> = _distance

    private val _estimatedFares = MutableStateFlow<Map<String, Int>>(emptyMap())
    val estimatedFares: StateFlow<Map<String, Int>> = _estimatedFares

    private val pricePerKm = mapOf(
        "Bike" to 5,
        "Car" to 10,
        "Shuttle" to 7,
        "E-Rickshaw" to 6
    )

    fun bookRide(rideMode: String) {
        viewModelScope.launch {
            println("Booking Ride in $rideMode mode. Distance: ${_distance.value} km")
        }
    }
}