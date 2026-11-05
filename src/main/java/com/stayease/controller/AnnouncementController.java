package com.stayease.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stayease.model.GuestAnnouncement;
import com.stayease.repository.GuestAnnouncementRepository;

@Controller
public class AnnouncementController {

    private final GuestAnnouncementRepository repository;

    public AnnouncementController(GuestAnnouncementRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/admin/announcements")
    public String announcements(Model model) {
        List<GuestAnnouncement> announcements =
                repository.findAllByOrderByCreatedAtDesc();

        model.addAttribute("announcements", announcements);
        return "admin-announcements";
    }

    @PostMapping("/admin/announcements/create")
    public String createAnnouncement(
            @RequestParam String title,
            @RequestParam String message,
            @RequestParam String targetFloor,
            @RequestParam String severity,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime expectedResolution,
            RedirectAttributes redirectAttributes) {

        if (title.isBlank() || message.isBlank()
                || targetFloor.isBlank()
                || !List.of("ALL", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
                        .contains(targetFloor)
                || !List.of("INFO", "WARNING", "URGENT").contains(severity)) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please enter valid announcement details.");
            return "redirect:/admin/announcements";
        }

        GuestAnnouncement announcement = new GuestAnnouncement();
        announcement.setTitle(title.trim());
        announcement.setMessage(message.trim());
        announcement.setTargetFloor(targetFloor);
        announcement.setSeverity(severity);
        announcement.setExpectedResolution(expectedResolution);
        announcement.setActive(true);

        repository.save(announcement);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Announcement published successfully!");

        return "redirect:/admin/announcements";
    }

    @PostMapping("/admin/announcements/deactivate")
    public String deactivateAnnouncement(
            @RequestParam Long id,
            RedirectAttributes redirectAttributes) {

        repository.findById(id).ifPresent(announcement -> {
            announcement.setActive(false);
            repository.save(announcement);
        });

        redirectAttributes.addFlashAttribute(
                "successMessage", "Announcement deactivated.");

        return "redirect:/admin/announcements";
    }
}