package com.example.parknride.DTO;

public class ParkingHistoryRequest {
    private String userId;
    private String parkingId;
    private String paymentId;
    private String name;
    private String location;
    private int pricePerHour;

    // Default Constructor
    public ParkingHistoryRequest() {
    }

    // Getters
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

    // Setters
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
}