package com.example.rideapp.models

data class RideHistory(
    val userId: String,
    val vehicleType: String,
    val distance: String,
    val fare: String,
    val paymentId: String
)