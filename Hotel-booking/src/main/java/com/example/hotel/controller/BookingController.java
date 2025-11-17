package com.example.hotel.controller;

import com.example.hotel.dto.BookingRequest;
import com.example.hotel.entity.Booking;
import com.example.hotel.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/rooms/{id}/book")
    public String bookRoom(@PathVariable Long id,
                           @AuthenticationPrincipal UserDetails user,
                           @Valid @ModelAttribute("bookingRequest") BookingRequest request,
                           BindingResult result,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to book a room");
            return "redirect:/login?next=/rooms/" + id;
        }
        request.setRoomId(id);
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fill all booking details");
            return "redirect:/rooms/" + id;
        }
        try {
            Booking booking = bookingService.createBooking(user.getUsername(), request);
            model.addAttribute("booking", booking);
            return "booking-confirm";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/rooms/" + id;
        }
    }
}

