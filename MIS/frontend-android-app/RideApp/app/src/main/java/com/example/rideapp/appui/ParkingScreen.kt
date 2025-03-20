package com.example.rideapp.appui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun ParkingScreen(navController: NavHostController, onPaymentSuccess: (ParkingSpot) -> Unit) {
    val context = LocalContext.current

    // Initialize Google Places API Client
    val placesClient = remember { Places.createClient(context) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var suggestions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    var selectedParking by remember { mutableStateOf<ParkingSpot?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(12.8232, 80.0457), 15f) // Default: SRM
    }

    // Parking locations with availability details
    val parkingLocations = remember {
        listOf(
            ParkingSpot("1", "Chennai Tech Park", LatLng(12.8232, 80.0457), mutableStateOf(2), "8 AM - 10 PM", 50),
            ParkingSpot("2", "SRM Dental Block", LatLng(12.8196, 80.0442), mutableStateOf(5), "8 AM - 10 PM", 40),
            ParkingSpot("3", "Main Campus Building", LatLng(12.8208, 80.0383), mutableStateOf(1), "8 AM - 10 PM", 30)
        )
    }

    fun fetchLocationSuggestions(query: String) {
        if (query.isEmpty()) {
            suggestions = emptyList()
            return
        }

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                suggestions = response.autocompletePredictions
            }
            .addOnFailureListener { exception ->
                Log.e("Places API", "Error: ${exception.localizedMessage}")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🚗 Parking") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Blue,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Box
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    fetchLocationSuggestions(it.text) // Fetch autocomplete suggestions
                },
                label = { Text("Search for parking...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // Show autocomplete suggestions
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                items(suggestions) { prediction ->
                    val placeName = prediction.getPrimaryText(null).toString()
                    Text(
                        text = placeName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                searchQuery = TextFieldValue(placeName)
                                suggestions = emptyList() // Hide suggestions
                            }
                            .padding(8.dp)
                    )
                }
            }

            // Google Map
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Show parking locations
                parkingLocations.forEach { parking ->
                    Marker(
                        state = MarkerState(position = parking.location),
                        title = "${parking.name} (Spots: ${parking.availableSpots.value})",
                        snippet = "Available",
                        onClick = {
                            selectedParking = parking
                            showDialog = true
                            true
                        }
                    )
                }
            }
        }
    }

    // Show booking dialog when a parking spot is clicked
    if (showDialog && selectedParking != null) {
        ParkingBookingDialog(selectedParking!!, onDismiss = { showDialog = false })
    }
}

// Parking Spot Data Class
data class ParkingSpot(
    val id: String,
    val name: String,
    val location: LatLng,
    var availableSpots: MutableState<Int>,
    val timing: String,
    val pricePerHour: Int
)