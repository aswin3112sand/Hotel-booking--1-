package com.example.hotel.controller;

import com.example.hotel.config.JwtUtil;
import com.example.hotel.dto.UserRegistrationDto;
import com.example.hotel.entity.Role;
import com.example.hotel.entity.User;
import com.example.hotel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtil jwt;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testApiLogin_Success() throws Exception {
        User user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .name("Test User")
                .role(Role.USER)
                .build();

        when(userService.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwt.generate(anyString(), any(Map.class))).thenReturn("mockToken");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mockToken"))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.roles").value("USER"));
    }

    @Test
    void testApiLogin_InvalidCredentials() throws Exception {
        when(userService.findByEmail("test@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid password"));
    }
}
