package com.example.parknride.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private Long parkingSlotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String qrCodeUrl;

    // Constructors
    public Reservation() {}

    public Reservation(String userId, Long parkingSlotId, LocalDateTime startTime, LocalDateTime endTime, String qrCodeUrl) {
        this.userId = userId;
        this.parkingSlotId = parkingSlotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.qrCodeUrl = qrCodeUrl;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getParkingSlotId() { return parkingSlotId; }
    public void setParkingSlotId(Long parkingSlotId) { this.parkingSlotId = parkingSlotId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
}