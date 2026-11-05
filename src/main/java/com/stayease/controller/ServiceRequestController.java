
package com.stayease.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stayease.model.FoodItem;
import com.stayease.model.GuestAnnouncement;
import com.stayease.model.ServiceRequest;
import com.stayease.repository.FoodItemRepository;
import com.stayease.repository.GuestAnnouncementRepository;
import com.stayease.repository.ServiceRequestRepository;

@Controller
public class ServiceRequestController {

    private final ServiceRequestRepository repository;
    private final FoodItemRepository foodItemRepository;
    private final GuestAnnouncementRepository announcementRepository;

    public ServiceRequestController(
            ServiceRequestRepository repository,
            FoodItemRepository foodItemRepository,
            GuestAnnouncementRepository announcementRepository) {
        this.repository = repository;
        this.foodItemRepository = foodItemRepository;
        this.announcementRepository = announcementRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        List<FoodItem> availableFood =
                foodItemRepository.findByAvailableTrueOrderByNameAsc();

        List<GuestAnnouncement> announcements =
                announcementRepository.findByActiveTrueOrderByCreatedAtDesc();

        model.addAttribute("foodItems", availableFood);
        model.addAttribute("announcements", announcements);

        return "index";
    }

    @PostMapping("/submit-request")
    public String submitRequest(@ModelAttribute ServiceRequest request) {

        ServiceRequest savedRequest = repository.save(request);

        return "redirect:/?success&id=" + savedRequest.getId();
    }

    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin-login";
    }

    @GetMapping("/admin")
    public String admin(
            @RequestParam(required = false) Long searchId,
            Model model) {

        List<ServiceRequest> allRequests = repository.findAll();

        List<ServiceRequest> displayedRequests;

        if (searchId != null) {
            ServiceRequest request = repository.findById(searchId).orElse(null);

            displayedRequests = request != null
                    ? Collections.singletonList(request)
                    : Collections.emptyList();

            model.addAttribute("searchPerformed", true);

        } else {
            displayedRequests = allRequests;
            model.addAttribute("searchPerformed", false);
        }

        model.addAttribute("allRequests", allRequests);
        model.addAttribute("requests", displayedRequests);
        model.addAttribute("searchId", searchId);

        return "admin";
    }

    @PostMapping("/admin/update-status")
    public String updateStatus(
            @RequestParam Long id,
            @RequestParam String status) {

        ServiceRequest request = repository.findById(id).orElse(null);

        if (request != null) {
            request.setStatus(status);
            repository.save(request);
        }

        return "redirect:/admin?searchId=" + id;
    }

    @PostMapping("/admin/assign-staff")
    public String assignStaff(
            @RequestParam Long id,
            @RequestParam String assignedStaff) {

        ServiceRequest request = repository.findById(id).orElse(null);

        if (request != null) {
            request.setAssignedStaff(
                    assignedStaff.isBlank() ? null : assignedStaff.trim());

            repository.save(request);
        }

        return "redirect:/admin?searchId=" + id;
    }

    @GetMapping("/track")
    public String trackRequest(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String roomNumber,
            Model model) {

        if (id != null && roomNumber != null && !roomNumber.isBlank()) {

            ServiceRequest request = repository
                    .findByIdAndRoomNumber(id, roomNumber)
                    .orElse(null);

            if (request != null) {
                model.addAttribute("request", request);
            } else {
                model.addAttribute("error",
                        "Request not found. Please check your details.");
            }
        }

        return "track";
    }
}
