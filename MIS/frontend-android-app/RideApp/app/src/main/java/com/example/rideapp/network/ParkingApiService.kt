package com.example.rideapp.network

import com.example.rideapp.models.ParkingHistory
import com.example.rideapp.models.ParkingHistorySummary
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ParkingApiService {
    @GET("parking-history/{userId}")
    suspend fun getParkingHistory(@Path("userId") userId: String): List<ParkingHistorySummary>

    @POST("parking-history")
    suspend fun storeParkingHistory(@Body parkingHistory: ParkingHistory): Response<Void>
}