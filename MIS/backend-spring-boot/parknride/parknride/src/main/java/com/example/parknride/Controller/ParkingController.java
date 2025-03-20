package com.example.parknride.Controller;

import com.example.parknride.Model.ParkingSlot;
import com.example.parknride.Model.Reservation;
import com.example.parknride.Service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingController {
    @Autowired
    private ParkingService parkingService;

    // ✅ Fetch available parking slots
    @GetMapping("/available-slots")
    public List<ParkingSlot> getAvailableSlots() {
        return parkingService.getAvailableSlots();
    }

    // ✅ Add a parking slot
    @PostMapping("/add-slot")
    public ResponseEntity<ParkingSlot> addParkingSlot(@RequestBody ParkingSlot slot) {
        ParkingSlot savedSlot = parkingService.addParkingSlot(slot);
        return ResponseEntity.ok(savedSlot);
    }

    // ✅ Book a parking slot
    @PostMapping("/book")
    public ResponseEntity<Reservation> bookSlot(@RequestBody Reservation request) {
        System.out.println("Received userId: " + request.getUserId());
        System.out.println("Received parkingSlotId: " + request.getParkingSlotId());
        Reservation reservation = parkingService.bookSlot(
                request.getUserId(), 1L,
                request.getStartTime(), request.getEndTime()
        );
        return ResponseEntity.ok(reservation);
    }

    // ✅ Cancel a reservation
    @DeleteMapping("/cancel/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        parkingService.cancelReservation(reservationId);
        return ResponseEntity.ok("Reservation cancelled successfully");
    }
}