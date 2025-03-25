package com.example.rideapp.models

data class ParkingHistory(
    val userId: String,
    val parkingId: String,
    val paymentId: String,
    val name: String,
    val location: String,
    val pricePerHour: Int,
    val timestamp: Long
)