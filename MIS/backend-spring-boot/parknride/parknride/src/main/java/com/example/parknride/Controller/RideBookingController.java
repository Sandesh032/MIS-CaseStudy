package com.example.parknride.Controller;

import com.example.parknride.Model.RideBooking;
import com.example.parknride.Service.RideBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideBookingController {

    @Autowired
    private RideBookingService rideBookingService;

    @PostMapping("/book")
    public ResponseEntity<RideBooking> bookRide(@RequestBody RideBooking rideBooking) {
        RideBooking savedRide = rideBookingService.bookRide(rideBooking);
        return ResponseEntity.ok(savedRide);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<RideBooking>> getUserRides(@PathVariable String userId) {
        List<RideBooking> rides = rideBookingService.getRidesByUserId(userId);
        return ResponseEntity.ok(rides);
    }
}