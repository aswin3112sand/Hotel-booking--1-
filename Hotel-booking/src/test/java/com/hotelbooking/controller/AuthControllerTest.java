package com.hotelbooking.controller;

import com.hotelbooking.model.User;
import com.hotelbooking.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void loginPageShowsErrorWhenParamPresent() throws Exception {
        mockMvc.perform(get("/login").param("error", ""))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("error"))
            .andExpect(view().name("login"));
    }

    @Test
    void registerSuccessReturnsLoginWithMessage() throws Exception {
        when(userService.register(any())).thenReturn(new User());

        mockMvc.perform(post("/register")
                .param("name", "Test User")
                .param("email", "test@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "secret123"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("msg"))
            .andExpect(view().name("login"));
    }

    @Test
    void registerDuplicateShowsError() throws Exception {
        when(userService.register(any())).thenThrow(new IllegalArgumentException("Email already exists"));

        mockMvc.perform(post("/register")
                .param("name", "Test User")
                .param("email", "test@example.com")
                .param("password", "secret123")
                .param("confirmPassword", "secret123"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("error", "Email already exists"))
            .andExpect(view().name("register"));
    }
}
