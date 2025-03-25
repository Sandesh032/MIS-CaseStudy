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
            // Save the ride history
            rideApiService.bookRide(rideHistory)
            true // Successful API call
        } catch (e: Exception) {
            Log.e("RideRepository", "Error saving ride history: ${e.localizedMessage}")
            false   // Unsuccessful API call
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