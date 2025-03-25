package com.example.rideapp.network

import com.example.rideapp.models.RideHistory
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RideApiService {
    @POST("rides/book")
    suspend fun bookRide(@Body rideHistory: RideHistory): RideHistory

    @GET("rides/history/{userId}")
    suspend fun getRideHistory(@Path("userId") userId: String): List<RideHistory>
}