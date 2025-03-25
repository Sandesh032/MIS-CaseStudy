package com.example.rideapp.models

data class RideHistorySummary(
    val id: Int,
    val userId: String,
    val vehicleType: String,
    val distance: String,
    val fare: String
)