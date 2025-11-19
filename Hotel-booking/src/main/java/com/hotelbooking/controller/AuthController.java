package com.hotelbooking.controller;

import com.hotelbooking.dto.UserRegistrationDto;
import com.hotelbooking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "next", required = false) String next,
                            @RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Authentication auth,
                            Model model){
        if (next != null && next.isBlank()) next = null;
        if (next != null && !next.startsWith("/")) next = null; // basic safety
        if (next != null) model.addAttribute("next", next);

        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }
        if (logout != null) {
            model.addAttribute("msg", "You have been logged out");
        }

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Already logged in: honor safe redirect if provided
            return "redirect:" + (next != null ? next : "/rooms");
        }
        return "login";
    }

    @GetMapping("/register")
    public String regPage(@RequestParam(value = "next", required = false) String next,
                          Model m){
        m.addAttribute("user", new UserRegistrationDto());
        if (next != null && !next.isBlank() && next.startsWith("/")) {
            m.addAttribute("next", next);
        }
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@Valid @ModelAttribute("user") UserRegistrationDto user,
                             BindingResult result,
                             @RequestParam(value = "next", required = false) String next,
                             Model m){
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.register(user);
        } catch (IllegalArgumentException ex) {
            m.addAttribute("error", ex.getMessage());
            return "register";
        }
        m.addAttribute("msg","Registration successful. Please login.");
        if (next != null && !next.isBlank() && next.startsWith("/")) {
            m.addAttribute("next", next);
        }
        return "login";
    }
}
