package com.hotelbooking.controller;

import com.hotelbooking.service.BookingService;
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
        if (user == null) {
            return "redirect:/login?next=/user/bookings";
        }
        model.addAttribute("bookings", bookingService.getBookingsForUser(user.getUsername()));
        return "booking/booking-history";
    }

    // Backward-compatible path from the old MyBookingsController
    @GetMapping("/my-bookings")
    public String legacyMyBookings() {
        return "redirect:/user/bookings";
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

