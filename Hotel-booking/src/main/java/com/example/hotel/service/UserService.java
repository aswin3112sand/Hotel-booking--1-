package com.example.hotel.service;

import com.example.hotel.dto.UserRegistrationDto;
import com.example.hotel.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User register(UserRegistrationDto dto);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    long countUsers();
}

