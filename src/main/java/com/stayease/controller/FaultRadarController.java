package com.stayease.controller;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.stayease.model.FaultAlert;
import com.stayease.repository.FaultAlertRepository;
import com.stayease.service.FaultRadarService;

@Controller
public class FaultRadarController {

    private final FaultRadarService faultRadarService;
    private final FaultAlertRepository faultAlertRepository;

    public FaultRadarController(
            FaultRadarService faultRadarService,
            FaultAlertRepository faultAlertRepository) {
        this.faultRadarService = faultRadarService;
        this.faultAlertRepository = faultAlertRepository;
    }

    @GetMapping("/admin/fault-radar")
    public String faultRadar(Model model) {

        faultRadarService.scanForFaults();

        List<FaultAlert> alerts = faultAlertRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        model.addAttribute("alerts", alerts);

        return "admin-fault-radar";
    }

    @PostMapping("/admin/fault-radar/update")
    public String updateAlert(
            @RequestParam Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        if (!status.equals("OPEN")
                && !status.equals("INVESTIGATING")
                && !status.equals("RESOLVED")) {
            redirectAttributes.addFlashAttribute(
                    "error", "Invalid alert status."
            );
            return "redirect:/admin/fault-radar";
        }

        FaultAlert alert = faultAlertRepository.findById(id).orElse(null);

        if (alert != null) {
            alert.setStatus(status);
            faultAlertRepository.save(alert);
        }

        return "redirect:/admin/fault-radar";
    }
}