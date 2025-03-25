package com.example.rideapp.appui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rideapp.R
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(navController: NavController) {
    var position by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        val duration = 1500L // Animation duration
        val steps = 50 // No. of steps
        val stepDelay = duration / steps
        val increment = 8f

        repeat(steps) {
            delay(stepDelay)
            position += increment
        }

        navController.navigate("login")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.car_image), // App logo
            contentDescription = "Vehicle",
            modifier = Modifier.offset(x = position.dp)
        )
    }
}