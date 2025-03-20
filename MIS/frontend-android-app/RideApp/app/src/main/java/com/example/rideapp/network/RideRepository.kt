package com.example.rideapp.network

import android.content.Context
import android.util.Log
import com.example.rideapp.models.RideHistory

object RideRepository {

    private val rideApiService: RideApiService = ApiClient.createService(RideApiService::class.java)

    suspend fun storeRideBookingHistory(
        userId: String,
        vehicleType: String,
        distance: String,
        fare: String,
        paymentId: String,
        context: Context
    ): Boolean {
        val rideHistory = RideHistory(userId, vehicleType, distance, fare, paymentId)

        return try {
            // Attempt to save the ride history (e.g., via an API call)
            rideApiService.bookRide(rideHistory) // Make the API call
            true // If the API call was successful
        } catch (e: Exception) {
            Log.e("RideRepository", "Error saving ride history: ${e.localizedMessage}")
            false // If there was an error
        }
    }

    suspend fun getUserRides(userId: String): List<RideHistory>? {
        return try {
            val response = rideApiService.getRideHistory(userId)
            response
        } catch (e: Exception) {
            Log.e("RideRepository", "Error fetching ride history: ${e.localizedMessage}")
            null
        }
    }
}