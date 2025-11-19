package com.hotelbooking.controller;

import com.hotelbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/bookings")
public class AdminBookingController {

    private final BookingService bookingService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("bookings", bookingService.getAllBookings());
        return "admin/all-bookings";
    }
}

