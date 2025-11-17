package com.example.hotel.controller;

import com.example.hotel.config.JwtUtil;
import com.example.hotel.dto.UserRegistrationDto;
import com.example.hotel.entity.Role;
import com.example.hotel.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthController(UserService userService, PasswordEncoder encoder, JwtUtil jwt) {
        this.userService = userService;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "next", required = false) String next,
                            Authentication auth,
                            Model model){
        if (next != null && next.isBlank()) next = null;
        if (next != null && !next.startsWith("/")) next = null; // basic safety
        if (next != null) model.addAttribute("next", next);

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Already logged in: honor safe redirect if provided
            return "redirect:" + (next != null ? next : "/rooms");
        }
        return "login";
    }
    
    // Fallback server-side login to work without JS
    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          @RequestParam(value = "next", required = false) String next,
                          Model m,
                          jakarta.servlet.http.HttpServletResponse response) {
        var userOpt = userService.findByEmail(email);
        var user = userOpt.orElse(null);
        if (user == null || !encoder.matches(password, user.getPassword())) {
            m.addAttribute("msg", null);
            m.addAttribute("error", "Invalid password");
            return "login";
        }
        Role role = user.getRole() == null ? Role.USER : user.getRole();
        Map<String,Object> claims = new HashMap<>();
        claims.put("roles", role.name());
        claims.put("name", user.getName());
        String token = jwt.generate(user.getEmail(), claims);

        // Set JWT cookie so subsequent requests are authenticated by JwtAuthFilter
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(6 * 60 * 60); // 6 hours
        cookie.setSecure(false); // set true when serving over HTTPS
        response.addCookie(cookie);

        // Prefer safe next if provided
        if (next != null && !next.isBlank() && next.startsWith("/")) {
            return "redirect:" + next;
        }
        if (role == Role.ADMIN) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/rooms";
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

    // Server-side logout for non-JS clients: clears JWT cookie
    @PostMapping("/logout")
    public String logout(jakarta.servlet.http.HttpServletResponse response) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("jwt", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // expire immediately
        cookie.setSecure(false); // set true when serving over HTTPS
        response.addCookie(cookie);
        // Redirect back to public rooms catalog after logout
        return "redirect:/rooms";
    }

    @PostMapping("/auth/login") @ResponseBody
    public ResponseEntity<Map<String, Object>> apiLogin(@RequestBody Map<String,String> b){
        String email = b.get("email");
        String pass = b.get("password");
        var u=userService.findByEmail(email).orElse(null);
        if(u==null||!encoder.matches(pass,u.getPassword()))
            return ResponseEntity.status(401).body(Map.of("error","Invalid password"));
        Map<String,Object> c=new HashMap<>();
        c.put("roles",u.getRole().name());
        c.put("name", u.getName());
        String token=jwt.generate(u.getEmail(),c);
        return ResponseEntity.ok(Map.of("token",token,"name",u.getName(),"roles",u.getRole().name()));
    }
}
