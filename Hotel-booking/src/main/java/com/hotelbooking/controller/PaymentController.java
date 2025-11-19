package com.hotelbooking.controller;

import com.hotelbooking.model.Payment;
import com.hotelbooking.model.PaymentMethod;
import com.hotelbooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/checkout/{bookingId}")
    public String checkout(@PathVariable Long bookingId,
                           @AuthenticationPrincipal UserDetails user,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (user == null) {
            return "redirect:/login?next=/payments/checkout/" + bookingId;
        }
        try {
            Payment payment = paymentService.ensurePayment(bookingId, user.getUsername());
            model.addAttribute("payment", payment);
            model.addAttribute("booking", payment.getBooking());
            model.addAttribute("paymentMethods", PaymentMethod.values());
            return "payment/checkout";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/user/bookings";
        }
    }

    @PostMapping("/{paymentId}/confirm")
    public String confirm(@PathVariable Long paymentId,
                          @RequestParam PaymentMethod method,
                          @AuthenticationPrincipal UserDetails user,
                          RedirectAttributes redirectAttributes) {
        if (user == null) {
            return "redirect:/login";
        }
        Long bookingId = null;
        try {
            Payment payment = paymentService.getPaymentForUser(paymentId, user.getUsername());
            bookingId = payment.getBooking().getId();
            payment = paymentService.markSuccess(paymentId, user.getUsername(), method);
            redirectAttributes.addFlashAttribute("msg", "Payment successful");
            return "redirect:/payments/success/" + payment.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            if (bookingId != null) {
                return "redirect:/payments/checkout/" + bookingId;
            }
            return "redirect:/user/bookings";
        }
    }

    @PostMapping("/{paymentId}/fail")
    public String fail(@PathVariable Long paymentId,
                       @AuthenticationPrincipal UserDetails user,
                       RedirectAttributes redirectAttributes) {
        if (user == null) {
            return "redirect:/login";
        }
        Long bookingId = null;
        try {
            Payment payment = paymentService.getPaymentForUser(paymentId, user.getUsername());
            bookingId = payment.getBooking().getId();
            payment = paymentService.markFailure(paymentId, user.getUsername(), "User cancelled");
            redirectAttributes.addFlashAttribute("error", "Payment marked as failed. Please try again.");
            return "redirect:/payments/failed/" + payment.getId();
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            if (bookingId != null) {
                return "redirect:/payments/checkout/" + bookingId;
            }
            return "redirect:/user/bookings";
        }
    }

    @GetMapping("/success/{paymentId}")
    public String success(@PathVariable Long paymentId,
                          @AuthenticationPrincipal UserDetails user,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        try {
            Payment payment = paymentService.getPaymentForUser(paymentId, user.getUsername());
            model.addAttribute("payment", payment);
            model.addAttribute("booking", payment.getBooking());
            return "payment/success";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/user/bookings";
        }
    }

    @GetMapping("/failed/{paymentId}")
    public String failed(@PathVariable Long paymentId,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (user == null) {
            return "redirect:/login";
        }
        try {
            Payment payment = paymentService.getPaymentForUser(paymentId, user.getUsername());
            model.addAttribute("payment", payment);
            model.addAttribute("booking", payment.getBooking());
            return "payment/failed";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/user/bookings";
        }
    }
}
