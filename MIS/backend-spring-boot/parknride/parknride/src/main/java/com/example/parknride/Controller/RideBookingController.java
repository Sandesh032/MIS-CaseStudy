package com.example.parknride.Controller;

import com.example.parknride.Model.RideBooking;
import com.example.parknride.Service.RideBookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
public class RideBookingController {

    @Autowired
    private RideBookingService rideBookingService;

    Logger logger = LoggerFactory.getLogger(RideBookingController.class);

    @PostMapping("/book")
    @CachePut(value = "bookride", key = "#rideBooking.userId")
    public RideBooking bookRide(@RequestBody RideBooking rideBooking) {
        RideBooking savedRide = rideBookingService.bookRide(rideBooking);
        logger.trace("Ride booking inserted with id: " + savedRide.getId());
        return savedRide;
    }

    @GetMapping("/history/{userId}")
    @Cacheable(value = "bookride", key = "#userId")
    public List<RideBooking> getUserRides(@PathVariable String userId) {
        List<RideBooking> rides = rideBookingService.getRidesByUserId(userId);
        logger.trace("Ride booking details requested for id: " + userId);
        return rides;
    }
}