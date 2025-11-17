package com.example.hotel.service.impl;

import com.example.hotel.dto.UserRegistrationDto;
import com.example.hotel.entity.Role;
import com.example.hotel.entity.User;
import com.example.hotel.repo.UserRepository;
import com.example.hotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(UserRegistrationDto dto) {
        if (!dto.passwordsMatch()) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        String email = dto.getEmail().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = User.builder()
                .name(dto.getName().trim())
                .email(email)
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public long countUsers() {
        return userRepository.count();
    }
}
