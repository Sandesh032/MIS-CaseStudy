package com.example.parknride.DTO;

public class ParkingHistoryDTO {
    private String name;
    private String location;
    private int pricePerHour;

    // Constructor
    public ParkingHistoryDTO(String name, String location, int pricePerHour) {
        this.name = name;
        this.location = location;
        this.pricePerHour = pricePerHour;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPricePerHour() {
        return pricePerHour;
    }

    public void setPricePerHour(int pricePerHour) {
        this.pricePerHour = pricePerHour;
    }
}