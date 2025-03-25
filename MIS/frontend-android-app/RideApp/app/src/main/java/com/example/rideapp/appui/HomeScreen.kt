package com.example.rideapp.appui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(auth: FirebaseAuth, navController: NavHostController) {
    val user = auth.currentUser

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Profile Icon in Top-Right Corner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                painter = rememberAsyncImagePainter(user?.photoUrl ?: ""),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("profile") // Navigate to ProfileScreen
                    }
            )
        }

        // Main Content - Welcome & Options
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome, ${user?.displayName ?: "User"}",
                fontSize = 22.sp,
                color = Color(0xFF2E3A59)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 🚗 Parking Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { navController.navigate("parking") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF64B5F6))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚗 Parking", fontSize = 20.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🚕 Ride Sharing Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable { navController.navigate("ride_sharing") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚕 Ride Sharing", fontSize = 20.sp, color = Color.White)
                }
            }
        }
    }
}