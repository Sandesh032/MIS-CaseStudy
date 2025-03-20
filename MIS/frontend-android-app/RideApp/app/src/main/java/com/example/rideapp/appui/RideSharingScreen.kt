package com.example.rideapp.appui

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rideapp.R
import com.example.rideapp.network.RideRepository
import com.example.rideapp.payment.PaymentActivity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.PlacesApi
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.*

import com.google.maps.model.LatLng as ApiLatLng

@Composable
fun RideSharingScreen(navController: NavHostController) {
    val context = LocalContext.current
    val apiKey = context.getString(R.string.google_maps_api_key)

    var startAddress by remember { mutableStateOf("") }
    var endAddress by remember { mutableStateOf("") }
    var startLocation by remember { mutableStateOf<LatLng?>(null) }
    var endLocation by remember { mutableStateOf<LatLng?>(null) }
    var startSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var endSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var totalDistance by remember { mutableStateOf<String?>(null) }
    var bookingMode by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedFare by remember { mutableStateOf<String?>(null) }


    val geoApiContext = remember { GeoApiContext.Builder().apiKey(apiKey).build() }
    val coroutineScope = rememberCoroutineScope()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(13.0827, 80.2707), 12f)
    }

    fun searchPlaces(query: String, isStart: Boolean) {
        if (query.isEmpty()) return
        coroutineScope.launch(Dispatchers.IO) {
            delay(300) // Debounce API call

            try {
                val request = PlacesApi.textSearchQuery(geoApiContext, query)
                    .location(ApiLatLng(13.0827, 80.2707)) // Chennai's LatLng
                    .radius(50000) // 50 km max

                val results = request.await().results
                val places = results.map { it.name }

                withContext(Dispatchers.Main) {
                    if (isStart) startSuggestions = places else endSuggestions = places
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    startSuggestions = emptyList()
                    endSuggestions = emptyList()
                }
            }
        }
    }

    fun getLatLngFromPlaceName(placeName: String, isStart: Boolean) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                val results = GeocodingApi.geocode(geoApiContext, placeName).await()
                if (results.isNotEmpty()) {
                    val location = results[0].geometry.location
                    val latLng = LatLng(location.lat, location.lng)
                    withContext(Dispatchers.Main) {
                        if (isStart) {
                            startLocation = latLng
                            startAddress = placeName
                            startSuggestions = emptyList()
                        } else {
                            endLocation = latLng
                            endAddress = placeName
                            endSuggestions = emptyList()
                        }
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 14f)
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun calculateDistance(): Double {
        if (startLocation == null || endLocation == null) return 0.0
        val R = 6371.0 // Earth's radius in km
        val lat1 = Math.toRadians(startLocation!!.latitude)
        val lon1 = Math.toRadians(startLocation!!.longitude)
        val lat2 = Math.toRadians(endLocation!!.latitude)
        val lon2 = Math.toRadians(endLocation!!.longitude)
        val dlat = lat2 - lat1
        val dlon = lon2 - lon1
        val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distance in km
    }

    fun selectVehicle(vehicle: String, fare: Double) {
        totalDistance = "%.2f km".format(calculateDistance())
        selectedFare = "${"%.2f".format(fare)}" // Just use the passed fare
        selectedVehicle = vehicle
    }

    fun calculateFare(distance: Double): String {
        if (distance == 0.0) return "Select locations first"
        val singlePricing = mapOf("Bike" to 25, "Auto" to 40, "Cab" to 80)
        val sharedPricing = mapOf("Auto" to 40 / 3.0, "Cab" to 80 / 4.0)
        return if (!bookingMode) {
            "Bike: ${"%.2f".format(distance * singlePricing["Bike"]!!)} | " +
                    "Auto: ${"%.2f".format(distance * singlePricing["Auto"]!!)} | " +
                    "Cab: ${"%.2f".format(distance * singlePricing["Cab"]!!)}"
        } else {
            "Auto (Shared): ${"%.2f".format(distance * sharedPricing["Auto"]!!)} | " +
                    "Cab (Shared): ${"%.2f".format(distance * sharedPricing["Cab"]!!)}"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = startAddress,
            onValueChange = {
                startAddress = it
                searchPlaces(it, true)
            },
            label = { Text("Start Location") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(startSuggestions) { suggestion ->
                Text(suggestion, modifier = Modifier
                    .clickable {
                        getLatLngFromPlaceName(suggestion, true)
                    }
                    .padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = endAddress,
            onValueChange = {
                endAddress = it
                searchPlaces(it, false)
            },
            label = { Text("End Location") },
            modifier = Modifier.fillMaxWidth()
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(endSuggestions) { suggestion ->
                Text(suggestion, modifier = Modifier
                    .clickable {
                        getLatLngFromPlaceName(suggestion, false)
                    }
                    .padding(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Smaller Map
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            cameraPositionState = cameraPositionState
        ) {
            startLocation?.let {
                Marker(state = MarkerState(position = it), title = "Start Location")
            }
            endLocation?.let {
                Marker(state = MarkerState(position = it), title = "End Location")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Toggle Booking Mode
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Single Booking")
            Switch(
                checked = bookingMode,
                onCheckedChange = { bookingMode = it }
            )
            Text("Shared Booking")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Vehicle Options
        Column {
            if (!bookingMode) {
                VehicleCard(
                    "Bike",
                    25.0 * calculateDistance(),
                    R.drawable.ic_bike,
                    selectedVehicle == "Bike"
                ) {
                    selectVehicle("Bike", 25.0 * calculateDistance())
                }
            }
            VehicleCard(
                "Auto",
                if (!bookingMode) 40.0 * calculateDistance() else (40.0 / 3.0) * calculateDistance(),
                R.drawable.ic_auto,
                selectedVehicle == "Auto"
            ) {
                selectVehicle(
                    "Auto",
                    if (!bookingMode) 40.0 * calculateDistance() else (40.0 / 3.0) * calculateDistance()
                )
            }
            VehicleCard(
                "Cab",
                if (!bookingMode) 80.0 * calculateDistance() else (80.0 / 4.0) * calculateDistance(),
                R.drawable.ic_cab,
                selectedVehicle == "Cab"
            ) {
                selectVehicle(
                    "Cab",
                    if (!bookingMode) 80.0 * calculateDistance() else (80.0 / 4.0) * calculateDistance()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calculate Fare Button
        Button(onClick = {
            if (selectedVehicle != null) {
                val distance = calculateDistance()
                totalDistance = "%.2f km".format(distance)
                showDialog = true
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Proceed")
        }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val paymentId = result.data?.getStringExtra("payment_id") ?: "Unknown"
                Toast.makeText(context, "Payment Successful! ID: $paymentId", Toast.LENGTH_LONG).show()

                CoroutineScope(Dispatchers.IO).launch {
                    // Fetch the userId from Firebase Authentication or another source
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"

                    // Now pass userId along with other parameters to the storeRideBookingHistory function
                    val success = RideRepository.storeRideBookingHistory(
                        userId = userId,  // Pass userId here
                        vehicleType = selectedVehicle ?: "Unknown",
                        distance = totalDistance ?: "Unknown",
                        fare = selectedFare ?: "0",
                        paymentId = paymentId,
                        context = context
                    )

                    // Check if the operation was successful
                    if (!success) {
                        Toast.makeText(context, "Failed to store ride booking history!", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context, "Payment Failed", Toast.LENGTH_LONG).show()
            }
            showDialog = false
        }

        // Booking Confirmation Dialog
        if (showDialog && selectedVehicle != null && selectedFare != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = {
                    Text(
                        text = "Confirm Your Ride",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "🚗 Vehicle: $selectedVehicle",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        totalDistance?.let {
                            Text(
                                text = "📏 Distance: $it",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Text(
                            text = "💰 Amount to Pay: $selectedFare",
                            style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF388E3C)),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            val amount = (selectedFare!!.toDouble() * 100).toInt()
                            val intent = Intent(context, PaymentActivity::class.java).apply {
                                putExtra("amount", amount)
                            }
                            launcher.launch(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2))
                    ) {
                        Text("✅ Book Ride", color = Color.White)
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("❌ Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun VehicleCard(
    vehicle: String,
    rate: Double,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFBBDEFB) else Color(0xFFE3F2FD) // Highlight selected vehicle
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "$vehicle icon",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(vehicle, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "${"%.2f".format(rate)}",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
    }
}