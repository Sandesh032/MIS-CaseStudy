package com.example.rideapp.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.rideapp.models.RideHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RideHistoryPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("RideHistoryPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveRideHistory(rideHistory: List<RideHistory>) {
        val json = gson.toJson(rideHistory)
        sharedPreferences.edit().putString("RIDE_HISTORY", json).apply()
    }

    fun getRideHistory(): List<RideHistory> {
        val json = sharedPreferences.getString("RIDE_HISTORY", null) ?: return emptyList()
        val type = object : TypeToken<List<RideHistory>>() {}.type
        return gson.fromJson(json, type)
    }
}