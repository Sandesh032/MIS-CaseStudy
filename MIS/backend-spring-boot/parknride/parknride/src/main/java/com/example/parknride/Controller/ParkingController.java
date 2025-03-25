package com.example.parknride.Controller;

import com.example.parknride.Model.ParkingSlot;
import com.example.parknride.Model.Reservation;
import com.example.parknride.Service.ParkingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    @Autowired
    private ParkingService parkingService;
    Logger logger = LoggerFactory.getLogger(ParkingController.class);

    @GetMapping("/available-slots")
    public List<ParkingSlot> getAvailableSlots() {
        logger.trace("Available slots requested!!");
        return parkingService.getAvailableSlots();
    }

    @PostMapping("/add-slot")
    public ResponseEntity<ParkingSlot> addParkingSlot(@RequestBody ParkingSlot slot) {
        logger.trace("Adding slots!!");
        ParkingSlot savedSlot = parkingService.addParkingSlot(slot);
        return ResponseEntity.ok(savedSlot);
    }

    @PostMapping("/book")
    public ResponseEntity<Reservation> bookSlot(@RequestBody Reservation request) {
        logger.trace("Booking requested!!");
        System.out.println("Received userId: " + request.getUserId());
        System.out.println("Received parkingSlotId: " + request.getParkingSlotId());
        Reservation reservation = parkingService.bookSlot(
                request.getUserId(), 1L,
                request.getStartTime(), request.getEndTime()
        );
        return ResponseEntity.ok(reservation);
    }

    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        logger.trace("Cancelling reservation having id: " + reservationId);
        parkingService.cancelReservation(reservationId);
        return ResponseEntity.ok("Reservation cancelled successfully");
    }
}