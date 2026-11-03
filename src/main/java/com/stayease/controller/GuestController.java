
package com.stayease.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.stayease.model.Guest;
import com.stayease.repository.GuestRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class GuestController {

    private final GuestRepository guestRepository;
    private final PasswordEncoder passwordEncoder;

    public GuestController(
            GuestRepository guestRepository,
            PasswordEncoder passwordEncoder) {

        this.guestRepository = guestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/guest/login")
    public String loginPage() {
        return "guest-login";
    }

    @PostMapping("/guest/login")
    public String login(
            @RequestParam String roomNumber,
            @RequestParam String pin,
            HttpSession session,
            Model model) {

        Guest guest = guestRepository
                .findByRoomNumber(roomNumber)
                .orElse(null);

        if (guest == null ||
                !guest.isActive() ||
                !passwordEncoder.matches(pin, guest.getPinHash())) {

            model.addAttribute("error",
                    "Invalid room number or PIN.");

            return "guest-login";
        }

        session.setAttribute("guestId", guest.getId());
        session.setAttribute("guestName", guest.getGuestName());
        session.setAttribute("roomNumber", guest.getRoomNumber());

        return "redirect:/guest/dashboard";
    }

    @GetMapping("/guest/dashboard")
    public String dashboard(HttpSession session, Model model) {

        if (session.getAttribute("guestId") == null) {
            return "redirect:/guest/login";
        }

        model.addAttribute("guestName",
                session.getAttribute("guestName"));

        model.addAttribute("roomNumber",
                session.getAttribute("roomNumber"));

        return "guest-dashboard";
    }

    @PostMapping("/guest/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/guest/login";
    }
}
