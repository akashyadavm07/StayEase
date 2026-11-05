
package com.stayease.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stayease.model.FaultAlert;
import com.stayease.model.ServiceRequest;
import com.stayease.repository.FaultAlertRepository;
import com.stayease.repository.ServiceRequestRepository;

@Service
public class FaultRadarService {

    private final ServiceRequestRepository serviceRequestRepository;
    private final FaultAlertRepository faultAlertRepository;

    public FaultRadarService(
            ServiceRequestRepository serviceRequestRepository,
            FaultAlertRepository faultAlertRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.faultAlertRepository = faultAlertRepository;
    }

    @Transactional
    public void scanForFaults() {

        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(30);

        List<ServiceRequest> requests = serviceRequestRepository.findAll();

        Map<String, List<ServiceRequest>> clusters = new HashMap<>();

        System.out.println("========== FAULT RADAR SCAN ==========");
        System.out.println("Total requests: " + requests.size());

        for (ServiceRequest request : requests) {

            if (request.getCreatedAt() == null ||
                    request.getCreatedAt().isBefore(cutoff) ||
                    request.getFloorNumber() == null ||
                    request.getFloorNumber().isBlank()) {
                continue;
            }

            String category = detectCategory(request.getDescription());

            if (category == null) {
                category = detectCategory(request.getServiceType());
            }

            System.out.println(
                    "Request ID: " + request.getId()
                    + " | Floor: " + request.getFloorNumber()
                    + " | Category: " + category
                    + " | Description: " + request.getDescription()
            );

            if (category == null) {
                continue;
            }

            String key = category + "|" + request.getFloorNumber().trim();

            clusters.computeIfAbsent(key, k -> new ArrayList<>()).add(request);
        }

        for (Map.Entry<String, List<ServiceRequest>> entry : clusters.entrySet()) {

            List<ServiceRequest> complaints = entry.getValue();

            System.out.println(
                    "Cluster: " + entry.getKey()
                    + " | Count: " + complaints.size()
            );

            if (complaints.size() < 3) {
                continue;
            }

            String[] parts = entry.getKey().split("\\|", 2);

            String category = parts[0];
            String floor = parts[1];

            FaultAlert alert = faultAlertRepository
                    .findFirstByIssueCategoryAndFloorNumberAndStatusNot(
                            category, floor, "RESOLVED")
                    .orElseGet(FaultAlert::new);

            alert.setIssueCategory(category);
            alert.setFloorNumber(floor);
            alert.setComplaintCount(complaints.size());

            alert.setDescription(
                    complaints.size() + " similar complaints detected on Floor "
                    + floor + " within the last 30 minutes. "
                    + "Possible shared infrastructure issue. Please investigate."
            );

            faultAlertRepository.save(alert);

            System.out.println("FAULT ALERT SAVED: " + category + " | Floor " + floor);
        }

        System.out.println("========== SCAN COMPLETED ==========");
    }

    private String detectCategory(String text) {

        if (text == null) {
            return null;
        }

        String value = text.toLowerCase().replaceAll("[^a-z0-9 ]", " ");

        if (value.contains("ac")
                || value.contains("air conditioner")
                || value.contains("cooling")
                || value.contains("warm air")
                || value.contains("hot air")
                || value.contains("not cool")
                || value.contains("hot room")) {
            return "AIR CONDITIONING";
        }

        if (value.contains("power")
                || value.contains("electricity")
                || value.contains("electrical")
                || value.contains("light")
                || value.contains("switch")
                || value.contains("socket")) {
            return "ELECTRICAL";
        }

        if (value.contains("water")
                || value.contains("leak")
                || value.contains("tap")
                || value.contains("faucet")
                || value.contains("shower")) {
            return "WATER";
        }

        if (value.contains("drain")
                || value.contains("toilet")
                || value.contains("flush")
                || value.contains("pipe")
                || value.contains("clog")) {
            return "PLUMBING";
        }

        if (value.contains("wifi")
                || value.contains("wi fi")
                || value.contains("internet")
                || value.contains("network")
                || value.contains("router")) {
            return "WIFI";
        }

        return null;
    }
}
