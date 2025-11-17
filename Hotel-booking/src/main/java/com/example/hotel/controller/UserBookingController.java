package com.example.hotel.controller;

import com.example.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user/bookings")
public class UserBookingController {

    private final BookingService bookingService;

    @GetMapping
    public String myBookings(@AuthenticationPrincipal UserDetails user,
                             Model model) {
        model.addAttribute("bookings", bookingService.getBookingsForUser(user.getUsername()));
        return "my-bookings";
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails user,
                                RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(user.getUsername(), id);
            redirectAttributes.addFlashAttribute("msg", "Booking cancelled");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/user/bookings";
    }
}

