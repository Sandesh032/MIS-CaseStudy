package com.example.rideapp.payment

import android.app.Application
import com.razorpay.Checkout

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Checkout.preload(applicationContext)
    }
}