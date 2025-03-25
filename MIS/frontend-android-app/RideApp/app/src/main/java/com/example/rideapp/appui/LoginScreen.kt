package com.example.rideapp.appui

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rideapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun LoginScreen(navController: NavHostController, auth: FirebaseAuth) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }     // To show circular loading icon

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                isLoading = true
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    isLoading = false  // Hide loading after authentication completes
                    if (task.isSuccessful) {
                        Log.d("GoogleSignIn", "Success")
                        navController.navigate("home") // Navigate to Home
                    } else {
                        Log.e("GoogleSignIn", "Failed", task.exception)
                    }
                }
            } catch (e: ApiException) {
                isLoading = false  // Hide loading in case of failure
                Log.e("GoogleSignIn", "Google sign in failed", e)
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Logo
        Image(
            painter = painterResource(id = R.drawable.car_image),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App Name
        Text(
            text = "Park & Ride",
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(40.dp))

        // Google Sign-In Button
        Row(
            modifier = Modifier
                .clickable {
                    if (!isLoading) signInWithGoogle(context, launcher)
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Icon",
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Sign in with Google", fontSize = 18.sp, color = Color.Black)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Show Circular progress indicator when loading
        if (isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

fun signInWithGoogle(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("623334048704-it053jblnlunnmtnqsam7n5f3s7le42c.apps.googleusercontent.com") // Replace with Firebase Web Client ID
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}