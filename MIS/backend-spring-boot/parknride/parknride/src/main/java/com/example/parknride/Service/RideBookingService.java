package com.example.parknride.Service;

import com.example.parknride.Model.RideBooking;
import com.example.parknride.Repo.RideBookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RideBookingService {

    @Autowired
    private RideBookingRepository rideBookingRepository;

    public RideBooking bookRide(RideBooking rideBooking) {
        return rideBookingRepository.save(rideBooking);
    }

    public List<RideBooking> getRidesByUserId(String userId) {
        return rideBookingRepository.findByUserId(userId);
    }
}