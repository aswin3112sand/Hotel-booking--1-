package com.hotelbooking.controller;

import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.RoomService;
import com.hotelbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping
    public String index() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("roomCount", roomService.countRooms());
        model.addAttribute("bookingCount", bookingService.countActiveBookings());
        model.addAttribute("confirmedBookings", bookingService.countConfirmedBookings());
        model.addAttribute("userCount", userService.countUsers());
        model.addAttribute("revenue", bookingService.totalRevenue());
        model.addAttribute("recentBookings", bookingService.getAllBookings().stream().limit(5).toList());
        return "admin/admin-dashboard";
    }
}

