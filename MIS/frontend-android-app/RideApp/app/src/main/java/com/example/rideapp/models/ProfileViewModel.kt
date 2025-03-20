package com.example.rideapp.models

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rideapp.network.ApiClient
import com.example.rideapp.network.RideRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.HttpException
import java.net.ConnectException

class ProfileViewModel(private val context: Context) : ViewModel() {

    private val _parkingHistory = MutableStateFlow<List<ParkingHistorySummary>>(emptyList())
    val parkingHistory: StateFlow<List<ParkingHistorySummary>> = _parkingHistory

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("history_prefs", Context.MODE_PRIVATE)

    private val _rideHistory = MutableStateFlow<List<RideHistory>>(emptyList())
    val rideHistory: StateFlow<List<RideHistory>> = _rideHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchParkingHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (!isInternetAvailable()) {
            Log.e("ProfileViewModel", "No internet connection. Loading from SharedPreferences")
            loadParkingHistoryFromPreferences()
            return
        }

        viewModelScope.launch {
            try {
                val response = ApiClient.parkingHistoryApi.getParkingHistory(userId)
                _parkingHistory.value = response

                // Save fetched data to SharedPreferences for offline use
                saveParkingHistoryToPreferences(response)
            } catch (e: ConnectException) {
                Log.e("ProfileViewModel", "No internet, loading from SharedPreferences", e)
                loadParkingHistoryFromPreferences()
            } catch (e: HttpException) {
                Log.e("ProfileViewModel", "HTTP Error: ${e.code()}", e)
                loadParkingHistoryFromPreferences()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Unknown Error", e)
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

    // Fetch Ride History
    fun fetchRideHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        if (!isInternetAvailable()) {
            Log.e("ProfileViewModel", "No internet connection. Loading from SharedPreferences")
            loadRideHistoryFromPreferences()
            return
        }

        viewModelScope.launch {
            try {
                val history = RideRepository.getUserRides(userId)
                if (history != null) {
                    _rideHistory.value = history

                    // Save fetched data to SharedPreferences for offline use
                    saveRideHistoryToPreferences(history)
                } else {
                    Log.e("ProfileViewModel", "Failed to fetch ride history")
                }
            } catch (e: ConnectException) {
                Log.e("ProfileViewModel", "No internet, loading from SharedPreferences", e)
                loadRideHistoryFromPreferences()
            } catch (e: HttpException) {
                Log.e("ProfileViewModel", "HTTP Error: ${e.code()}", e)
                loadRideHistoryFromPreferences()
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Unknown Error", e)
                loadRideHistoryFromPreferences()
            }
        }
    }

    private fun saveRideHistoryToPreferences(history: List<RideHistory>) {
        val json = Gson().toJson(history)
        sharedPreferences.edit().putString("ride_history", json).apply()
    }

    private fun loadRideHistoryFromPreferences() {
        val json = sharedPreferences.getString("ride_history", null)
        if (json != null) {
            val type = object : TypeToken<List<RideHistory>>() {}.type
            _rideHistory.value = Gson().fromJson(json, type)
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}