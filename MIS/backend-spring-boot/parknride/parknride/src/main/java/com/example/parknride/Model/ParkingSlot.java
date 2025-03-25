package com.example.parknride.Model;

import jakarta.persistence.*;

@Entity
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    @Column(nullable = false)
    private boolean isAvailable = true;

    private String type;

    @Column(nullable = false)
    private int totalSlots;  // Total slots in this parking area

    @Column(nullable = false)
    private int availableSlots;  // Current available slots

    // Constructors
    public ParkingSlot() {
    }

    public ParkingSlot(String location, boolean isAvailable, String type, int totalSlots) {
        this.location = location;
        this.isAvailable = isAvailable;
        this.type = type;
        this.totalSlots = totalSlots;
        this.availableSlots = totalSlots;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
        this.availableSlots = totalSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = totalSlots;
    }

    // Method to decrease available slots and update isAvailable status
    public boolean bookSlot() {
        if (availableSlots > 0) {
            availableSlots--;
            if (availableSlots == 0) {
                isAvailable = false;
            }
            return true;
        }
        return false; // No available slots left
    }

    // Method to release a slot upon cancellation
    public void releaseSlot() {
        if (availableSlots < totalSlots) {
            availableSlots++;
            isAvailable = true; // Re-enable booking if slots become available
        }
    }
}