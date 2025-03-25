package com.example.rideapp.network

import android.app.Activity
import android.util.Log
import com.example.rideapp.appui.ParkingSpot
import com.example.rideapp.models.ParkingHistory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ParkingRepository {
    private val apiService = ApiClient.createService(ParkingApiService::class.java)

    suspend fun storeParkingHistory(parkingSpot: ParkingSpot, paymentId: String, context: Activity): Boolean {
        return withContext(Dispatchers.IO) { // Run in background
            try {
                val history = ParkingHistory(
                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown",
                    parkingId = parkingSpot.id,
                    paymentId = paymentId,
                    name = parkingSpot.name,
                    location = "${parkingSpot.location.latitude}, ${parkingSpot.location.longitude}",
                    pricePerHour = parkingSpot.pricePerHour,
                    timestamp = System.currentTimeMillis()
                )

                val response = apiService.storeParkingHistory(history)
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to store parking history: ${e.localizedMessage}")
                false
            }
        }
    }
}