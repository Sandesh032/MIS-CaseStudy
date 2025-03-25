package com.example.parknride.Service;

import com.example.parknride.Model.ParkingSlot;
import com.example.parknride.Model.Reservation;
import com.example.parknride.Repo.ParkingSlotRepository;
import com.example.parknride.Repo.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {
    @Autowired
    private ParkingSlotRepository parkingSlotRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    public ParkingSlot addParkingSlot(ParkingSlot slot) {
        slot.setAvailableSlots(slot.getTotalSlots()); // Ensure availableSlots is initialized
        return parkingSlotRepository.save(slot);
    }

    public List<ParkingSlot> getAvailableSlots() {
        return parkingSlotRepository.findAll();
    }

    public Reservation bookSlot(String userId, Long parkingSlotId, LocalDateTime startTime, LocalDateTime endTime) {
        if (parkingSlotId == null) {
            throw new IllegalArgumentException("Parking slot ID cannot be null");
        }

        Optional<ParkingSlot> optionalSlot = parkingSlotRepository.findById(parkingSlotId);

        if (optionalSlot.isEmpty()) {
            throw new RuntimeException("Parking slot not found");
        }

        ParkingSlot slot = optionalSlot.get();

        // Check if slot is available
        if (slot.getAvailableSlots() <= 0) {
            throw new RuntimeException("No available slots left for this parking area");
        }

        // Book the slot (decrease available slots)
        slot.bookSlot();
        parkingSlotRepository.save(slot);

        // Generate QR Code URL using QRServer API
        String qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" +
                "ParkingReservation-" + userId + "-" + System.currentTimeMillis();

        // Create and save reservation
        Reservation reservation = new Reservation(userId, parkingSlotId, startTime, endTime, qrCodeUrl);
        return reservationRepository.save(reservation);
    }

    public void cancelReservation(Long reservationId) {
        Optional<Reservation> reservationOpt = reservationRepository.findById(reservationId);

        if (reservationOpt.isPresent()) {
            Reservation reservation = reservationOpt.get();
            Optional<ParkingSlot> slotOpt = parkingSlotRepository.findById(reservation.getParkingSlotId());

            if (slotOpt.isPresent()) {
                ParkingSlot slot = slotOpt.get();
                slot.releaseSlot(); // Increase available slots
                parkingSlotRepository.save(slot);
            }
            reservationRepository.delete(reservation);
        } else {
            throw new RuntimeException("Reservation not found");
        }
    }
}