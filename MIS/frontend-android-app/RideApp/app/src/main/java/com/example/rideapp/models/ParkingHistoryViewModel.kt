package com.example.rideapp.models

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideapp.network.ApiClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.net.ConnectException

class ParkingHistoryViewModel(context: Context) : ViewModel() {

    private val _parkingHistory = MutableStateFlow<List<ParkingHistorySummary>>(emptyList())
    val parkingHistory: StateFlow<List<ParkingHistorySummary>> = _parkingHistory

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("parking_history_prefs", Context.MODE_PRIVATE)

    fun fetchParkingHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val response = ApiClient.parkingHistoryApi.getParkingHistory(userId)
                _parkingHistory.value = response

                // Save fetched data to SharedPreferences
                saveParkingHistoryToPreferences(response)
            } catch (e: ConnectException) {
                Log.e("ParkingHistoryViewModel", "No internet, loading from local storage")
                loadParkingHistoryFromPreferences()
            } catch (e: HttpException) {
                Log.e("ParkingHistoryViewModel", "HTTP Error: ${e.code()}", e)
                loadParkingHistoryFromPreferences()
            } catch (e: Exception) {
                Log.e("ParkingHistoryViewModel", "Unknown Error", e)
                loadParkingHistoryFromPreferences()
            }
        }
    }

    private fun saveParkingHistoryToPreferences(history: List<ParkingHistorySummary>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit().putString("parking_history", json).apply()
    }

    private fun loadParkingHistoryFromPreferences() {
        val json = sharedPreferences.getString("parking_history", null)
        if (json != null) {
            val type = object : TypeToken<List<ParkingHistorySummary>>() {}.type
            _parkingHistory.value = Gson().fromJson(json, type)
        }
    }
}