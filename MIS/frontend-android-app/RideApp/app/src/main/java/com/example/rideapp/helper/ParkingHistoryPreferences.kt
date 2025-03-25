package com.example.rideapp.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.rideapp.models.ParkingHistorySummary
import com.example.rideapp.models.RideHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("HistoryPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Save parking history
    fun saveParkingHistory(parkingHistory: List<ParkingHistorySummary>) {
        val json = gson.toJson(parkingHistory)
        sharedPreferences.edit().putString("PARKING_HISTORY", json).apply()
    }

    // Get parking history
    fun getParkingHistory(): List<ParkingHistorySummary> {
        val json = sharedPreferences.getString("PARKING_HISTORY", null) ?: return emptyList()
        val type = object : TypeToken<List<ParkingHistorySummary>>() {}.type
        return gson.fromJson(json, type)
    }

    // Save ride history
    fun saveRideHistory(rideHistory: List<RideHistory>) {
        val json = gson.toJson(rideHistory)
        sharedPreferences.edit().putString("RIDE_HISTORY", json).apply()
    }

    // Get ride history
    fun getRideHistory(): List<RideHistory> {
        val json = sharedPreferences.getString("RIDE_HISTORY", null) ?: return emptyList()
        val type = object : TypeToken<List<RideHistory>>() {}.type
        return gson.fromJson(json, type)
    }
}