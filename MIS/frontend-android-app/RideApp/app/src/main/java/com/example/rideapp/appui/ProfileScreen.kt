package com.example.rideapp.appui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.rideapp.models.ParkingHistorySummary
import com.example.rideapp.models.ProfileViewModel
import com.example.rideapp.models.RideHistory
import com.example.rideapp.models.RideHistorySummary
import com.example.rideapp.models.RideHistoryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    auth: FirebaseAuth,
    navController: NavHostController,
    viewModel: ProfileViewModel
) {
    val parkingHistory by viewModel.parkingHistory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val rideHistory by viewModel.rideHistory.collectAsState()
    val isRideHistoryLoading by viewModel.isLoading.collectAsState()

    val user = auth.currentUser

    LaunchedEffect(Unit) {
        viewModel.fetchParkingHistory()
        user?.uid?.let { viewModel.fetchRideHistory() }
    }

    // Wrap the entire content in a LazyColumn for scrolling
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentPadding = PaddingValues(bottom = 30.dp) // To give some space for the Sign Out button at the bottom
    ) {
        item {
            // Profile Image
            Image(
                painter = rememberAsyncImagePainter(user?.photoUrl ?: ""),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Name
            Text(
                text = user?.displayName ?: "N/A",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )

            Spacer(modifier = Modifier.height(5.dp))

            // Email
            Text(
                text = user?.email ?: "N/A",
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(15.dp))

            // 🅿️ Parking History Section
            Text(
                text = "Parking History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (parkingHistory.isNotEmpty()) {
                ParkingHistoryList(parkingHistory)
            } else {
                Text(text = "No parking history found.", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Ride History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E3A59)
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (isRideHistoryLoading) {
                CircularProgressIndicator()
            } else if (rideHistory.isNotEmpty()) {
                RideHistoryList(rideHistory)
            } else {
                Text(text = "No ride history found.", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Sign Out Button
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sign Out", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun RideHistoryList(rideHistory: List<RideHistory>) {
    // Filter out the ride history items where distance is "Unknown"
    val filteredRideHistory = rideHistory.filter { it.distance != "Unknown" }

    Column {
        filteredRideHistory.forEach { ride ->
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚗 Vehicle: ${ride.vehicleType}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text("📍 Distance: ${ride.distance}", fontSize = 16.sp, color = Color.Gray)
                    Text("💰 ₹${ride.fare}", fontSize = 16.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun ParkingHistoryList(parkingHistory: List<ParkingHistorySummary>) {
    Column {
        parkingHistory.forEach { history ->
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📍 ${history.name}", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                    Text("📍 Location: ${history.location}", fontSize = 16.sp, color = Color.Gray)
                    Text("💰 ₹${history.pricePerHour}/hr", fontSize = 16.sp, color = Color.Gray)
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}