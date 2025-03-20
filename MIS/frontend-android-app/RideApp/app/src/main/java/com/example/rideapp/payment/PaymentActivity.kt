package com.example.rideapp.payment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.rideapp.models.RideHistory
import com.example.rideapp.network.RideRepository
import com.google.firebase.auth.FirebaseAuth
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PaymentActivity : AppCompatActivity(), PaymentResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val amount = intent.getIntExtra("amount", 0)

        if (amount > 0) {
            startPayment(amount)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun startPayment(amount: Int) {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_9YcQ0P4jxs8sQ6") // Replace with actual Razorpay Key

        try {
            val options = JSONObject().apply {
                put("name", "Park & Ride")
                put("description", "Parking Slot Booking")
                put("currency", "INR")
                put("amount", amount)
                put("prefill", JSONObject().apply {
                    put("email", "user@example.com") // Prefill email (optional)
                    put("contact", "9999999999") // Prefill phone number (optional)
                })
            }

            checkout.open(this, options)

        } catch (e: Exception) {
            Log.e("Razorpay", "Error in payment: ${e.localizedMessage}")
            Toast.makeText(this, "Payment Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "Unknown"

        val intent = Intent().apply {
            putExtra("payment_id", paymentId ?: "Unknown")
        }
        setResult(Activity.RESULT_OK, intent)

        val rideHistory = RideHistory(
            userId = userId,  // Add userId here
            vehicleType = intent.getStringExtra("vehicleType") ?: "Unknown",
            distance = intent.getStringExtra("distance") ?: "Unknown",
            fare = intent.getStringExtra("fare") ?: "0",
            paymentId = paymentId ?: "Unknown"
        )

        CoroutineScope(Dispatchers.IO).launch {
            val success = RideRepository.storeRideBookingHistory(
                userId = rideHistory.userId,
                vehicleType = rideHistory.vehicleType,
                distance = rideHistory.distance,
                fare = rideHistory.fare,
                paymentId = rideHistory.paymentId,
                context = this@PaymentActivity
            )

            // If success, set the result as OK, otherwise CANCELED
            if (success) {
                setResult(Activity.RESULT_OK)
            } else {
                setResult(Activity.RESULT_CANCELED)
            }

            finish()
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.e("Razorpay", "Payment failed: $response")
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}