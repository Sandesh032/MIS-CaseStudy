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
        checkout.setKeyID("rzp_test_9YcQ0P4jxs8sQ6")

        try {
            val options = JSONObject().apply {
                put("name", "Park & Ride")
                put("description", "Parking Slot Booking")
                put("currency", "INR")
                put("amount", amount)
                put("prefill", JSONObject().apply {     // Only for testing purpose
                    put("email", "user@example.com")
                    put("contact", "9999999999")
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
            userId = userId,
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

            // If successful, set payment status to ok
            if (success) {
                setResult(Activity.RESULT_OK)
            } else {    // Else, set to cancelled
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