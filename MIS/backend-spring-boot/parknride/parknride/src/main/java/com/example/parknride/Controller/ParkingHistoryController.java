package com.example.parknride.Controller;

import com.example.parknride.DTO.ParkingHistoryDTO;
import com.example.parknride.DTO.ParkingHistoryRequest;
import com.example.parknride.Model.ParkingHistory;
import com.example.parknride.Service.ParkingHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/parking-history")
public class ParkingHistoryController {

    @Autowired
    private final ParkingHistoryService service;
    Logger logger = LoggerFactory.getLogger(ParkingHistoryController.class);

    public ParkingHistoryController(ParkingHistoryService parkingHistoryService) {
        this.service = parkingHistoryService;
    }

    @PostMapping
    public ResponseEntity<ParkingHistory> storeParkingHistory(@RequestBody ParkingHistoryRequest request) {
        ParkingHistory savedHistory = service.saveParkingHistory(request);
        logger.trace("Parking history inserted with id: " + savedHistory.getParkingId());
        return ResponseEntity.ok(savedHistory);
    }

    @GetMapping("/{userId}")
    public List<ParkingHistoryDTO> getParkingHistory(@PathVariable String userId) {
        List<ParkingHistory> fullHistory = service.getParkingHistoryByUserId(userId);
        logger.trace("Parking history requested for id: " + userId);
        // Convert to summary DTO
        return fullHistory.stream()
                .map(history -> new ParkingHistoryDTO(history.getName(), history.getLocation(), history.getPricePerHour()))
                .collect(Collectors.toList());
    }
}