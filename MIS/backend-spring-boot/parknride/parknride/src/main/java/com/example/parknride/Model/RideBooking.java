package com.example.parknride.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "ride_bookings")
public class RideBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String vehicleType;
    private String distance;
    private String fare;
    private String paymentId;

    public RideBooking() {}

    public RideBooking(String userId, String vehicleType, String distance, String fare, String paymentId) {
        this.userId = userId;
        this.vehicleType = vehicleType;
        this.distance = distance;
        this.fare = fare;
        this.paymentId = paymentId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getDistance() { return distance; }
    public void setDistance(String distance) { this.distance = distance; }

    public String getFare() { return fare; }
    public void setFare(String fare) { this.fare = fare; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
}