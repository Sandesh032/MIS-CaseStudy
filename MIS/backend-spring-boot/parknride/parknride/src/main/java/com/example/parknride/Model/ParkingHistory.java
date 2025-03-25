package com.example.parknride.Model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "parking_history")
public class ParkingHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // Firebase UID

    @Column(nullable = false)
    private String parkingId;

    @Column(nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location; // "latitude, longitude"

    @Column(nullable = false)
    private int pricePerHour;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();

    // Default Constructor
    public ParkingHistory() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getParkingId() {
        return parkingId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPricePerHour(int pricePerHour) {
        this.pricePerHour = pricePerHour;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}