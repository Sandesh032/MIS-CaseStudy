package com.example.rideapp.appui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.rideapp.models.ParkingSpot
import com.example.rideapp.network.ParkingRepository
import com.example.rideapp.payment.PaymentActivity
import com.razorpay.Checkout
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStream

@SuppressLint("ContextCastToActivity")
@Composable
fun ParkingBookingDialog(navController: NavController, parkingSpot: ParkingSpot, onDismiss: () -> Unit) {
    val context = LocalContext.current as Activity
    var paymentSuccess by remember { mutableStateOf(false) }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var paymentId by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            paymentId = result.data?.getStringExtra("payment_id") ?: "Unknown"
            Toast.makeText(context, "Payment Successful! ID: $paymentId", Toast.LENGTH_LONG).show()

            // Reduce available slots
            if (parkingSpot.availableSpots.value > 0) {
                parkingSpot.availableSpots.value -= 1
            }

            // Generate QR Code
            qrBitmap =
                generateQRCode("Booking ID: $paymentId\nLocation: ${parkingSpot.name}\nTime: ${parkingSpot.timing}")
            paymentSuccess = true

            // Call API to save booking history
            coroutineScope.launch {
                val success = ParkingRepository.storeParkingHistory(
                    parkingSpot,
                    paymentId ?: "Unknown",
                    context
                )
                if (!success) {
                    Toast.makeText(context, "Failed to store booking history!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        } else {
            Toast.makeText(context, "Payment Failed", Toast.LENGTH_LONG).show()
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Book Parking Slot") },
        text = {
            Column {
                Text("📍 Location: ${parkingSpot.name}")
                Text("🚗 Available Spots: ${parkingSpot.availableSpots.value}")
                Text("⏰ Timing: ${parkingSpot.timing}")
                Text("💰 Price per Hour: ₹${parkingSpot.pricePerHour}")

                if (parkingSpot.availableSpots.value == 0) {
                    Text(
                        "❌ No available slots",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (paymentSuccess && qrBitmap != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Image(
                        bitmap = qrBitmap!!.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(200.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            if (!paymentSuccess) {
                Button(
                    onClick = {
                        val amount = parkingSpot.pricePerHour * 100 // Convert to paise
                        val intent = Intent(context, PaymentActivity::class.java).apply {
                            putExtra("amount", amount)
                        }
                        launcher.launch(intent)
                    },
                    enabled = parkingSpot.availableSpots.value > 0
                ) {
                    Text("Proceed to Pay")
                }
            } else {
                Button(
                    onClick = {
                        qrBitmap?.let { saveQRCodeToStorage(it, context) }
                    }
                ) {
                    Text("Download QR Code")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        navController.navigate("profile")
                    }
                ) {
                    Text("Go to Profile")
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

// Razorpay Payment Function
fun startPayment(activity: Activity, amount: Int, onDismiss: () -> Unit) {
    val checkout = Checkout()
    checkout.setKeyID("rzp_test_9YcQ0P4jxs8sQ6")

    try {
        val options = JSONObject().apply {
            put("name", "Park & Ride")
            put("description", "Parking Slot Booking")
            put("currency", "INR")
            put("amount", amount)
        }

        checkout.open(activity, options)
    } catch (e: Exception) {
        Log.e("Razorpay", "Error in payment: ${e.localizedMessage}")
    }
}

// Generate QR Code
fun generateQRCode(text: String): Bitmap? {
    return try {
        val writer = com.google.zxing.qrcode.QRCodeWriter()
        val bitMatrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, 300, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        bitmap
    } catch (e: Exception) {
        Log.e("QR", "Error generating QR Code: ${e.localizedMessage}")
        null
    }
}

// Save QR Code as Image
fun saveQRCodeToStorage(bitmap: Bitmap, context: Context) {
    val filename = "Parking_QR_Code_${System.currentTimeMillis()}.png"
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, filename)
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES
        ) // Saves it in the gallery
    }

    val resolver = context.contentResolver
    val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    imageUri?.let { uri ->
        try {
            val outputStream: OutputStream? = resolver.openOutputStream(uri)
            outputStream?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.flush()
            }
            Toast.makeText(context, "QR Code saved to Pictures", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("QR", "Error saving QR Code: ${e.localizedMessage}")
            Toast.makeText(context, "Error saving QR Code", Toast.LENGTH_LONG).show()
        }
    } ?: Toast.makeText(context, "Failed to save QR Code", Toast.LENGTH_LONG).show()
}