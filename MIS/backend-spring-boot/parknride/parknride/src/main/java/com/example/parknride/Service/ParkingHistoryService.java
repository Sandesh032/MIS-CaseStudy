package com.example.parknride.Service;

import com.example.parknride.DTO.ParkingHistoryRequest;
import com.example.parknride.Model.ParkingHistory;
import com.example.parknride.Repo.ParkingHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParkingHistoryService {
    @Autowired
    private final ParkingHistoryRepository repository;

    Logger logger = LoggerFactory.getLogger(ParkingHistoryService.class);

    public ParkingHistoryService(ParkingHistoryRepository parkingHistoryRepository) {
        this.repository = parkingHistoryRepository;
    }

    public List<ParkingHistory> getParkingHistoryByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    public ParkingHistory saveParkingHistory(ParkingHistoryRequest request) {
        ParkingHistory history = new ParkingHistory();
        history.setUserId(request.getUserId());
        history.setParkingId(request.getParkingId());
        history.setPaymentId(request.getPaymentId());
        history.setName(request.getName());
        history.setLocation(request.getLocation());
        history.setPricePerHour(request.getPricePerHour());

        return repository.save(history);
    }
}