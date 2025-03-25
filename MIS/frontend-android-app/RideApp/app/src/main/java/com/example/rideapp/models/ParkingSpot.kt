package com.example.rideapp.models

import androidx.compose.runtime.MutableState
import com.google.android.gms.maps.model.LatLng

data class ParkingSpot(
    val id: String,
    val name: String,
    val location: LatLng,
    var availableSpots: MutableState<Int>,
    val timing: String,
    val pricePerHour: Int
)