package com.hotelbooking.controller;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

  private final RoomService roomService;
  private final BookingService bookingService;

  @GetMapping("/")
  public String landing(Model model) {
    model.addAttribute("featuredRooms", roomService.featuredRooms(8));
    model.addAttribute("latestRooms", roomService.latestRooms(4));
    model.addAttribute("bookingRequest", new BookingRequest());
    long totalRooms = roomService.countRooms();
    long activeBookings = bookingService.countActiveBookings();
    long confirmedBookings = bookingService.countConfirmedBookings();
    long occupancyPercent = totalRooms == 0 ? 0 : Math.min(100, Math.round((double) activeBookings * 100 / totalRooms));
    model.addAttribute("activeBookings", activeBookings);
    model.addAttribute("confirmedBookings", confirmedBookings);
    model.addAttribute("totalRooms", totalRooms);
    model.addAttribute("occupancyPercent", occupancyPercent);
    model.addAttribute("recentBookings",
        bookingService.getAllBookings().stream().limit(3).toList());
    return "home";
  }

  @GetMapping("/home")
  public String homeRedirect() {
    return "redirect:/";
  }
}
