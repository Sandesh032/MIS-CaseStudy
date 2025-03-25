package com.example.rideapp.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    const val BASE_URL = "http://10.3.169.207:8080/api/"

    private val httpClient = OkHttpClient.Builder().build()

    // Retrofit instance
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }

    val parkingHistoryApi: ParkingApiService by lazy {
        retrofit.create(ParkingApiService::class.java)
    }

    val rideApi: RideApiService by lazy {
        retrofit.create(RideApiService::class.java)
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }
}