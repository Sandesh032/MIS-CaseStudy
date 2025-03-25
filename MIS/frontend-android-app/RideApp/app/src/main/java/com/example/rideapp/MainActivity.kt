package com.example.rideapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rideapp.appui.*
import com.example.rideapp.models.ParkingSpot
import com.example.rideapp.models.ProfileViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.android.libraries.places.api.Places

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        val auth = FirebaseAuth.getInstance()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyC9x4wPSwXEeKF3sTjtCu7bBZYnp-e9sDM")
        }

        setContent {
            val navController = rememberNavController()
            var user by remember { mutableStateOf(auth.currentUser) }

            var parkingHistory by remember { mutableStateOf<List<ParkingSpot>>(emptyList()) }

            fun handlePaymentSuccess(parkingSpot: ParkingSpot) {
                parkingHistory = parkingHistory + parkingSpot
            }

            NavHost(navController, startDestination = "loading") {
                composable("loading") { LoadingScreen(navController) }
                composable("login") { LoginScreen(navController, auth) }
                composable("home") { HomeScreen(auth, navController) }
                composable("profile") {
                    val context = LocalContext.current
                    val viewModel: ProfileViewModel =
                        viewModel(factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return ProfileViewModel(context) as T
                            }
                        })

                    val parkingHistory by viewModel.parkingHistory.collectAsState()
                    val rideHistory by viewModel.rideHistory.collectAsState()

                    ProfileScreen(auth, navController, viewModel)
                }
                composable("parking") {
                    ParkingScreen(navController, ::handlePaymentSuccess)
                }
                composable("ride_sharing") {
                    RideSharingScreen(navController)
                }
            }

            // Redirect user based on authentication state
            LaunchedEffect(user) {
                if (user != null) {
                    navController.navigate("home") {
                        popUpTo("loading") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("loading") { inclusive = true }
                    }
                }
            }
        }
    }
}