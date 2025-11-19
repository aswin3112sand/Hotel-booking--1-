package com.hotelbooking.service;

import com.hotelbooking.dto.UserRegistrationDto;
import com.hotelbooking.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(UserRegistrationDto dto);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    long countUsers();
}

