package com.example.hotel.service;

import com.example.hotel.dto.UserRegistrationDto;
import com.example.hotel.entity.Role;
import com.example.hotel.entity.User;
import com.example.hotel.repo.UserRepository;
import com.example.hotel.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void register_createsUserWithEncodedPassword() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("Jane");
        dto.setEmail("jane@example.com");
        dto.setPassword("secret123");
        dto.setConfirmPassword("secret123");

        when(passwordEncoder.encode("secret123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.register(dto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(saved.getEmail()).isEqualTo("jane@example.com");
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded");
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void register_throwsWhenPasswordsMismatch() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("Jane");
        dto.setEmail("jane@example.com");
        dto.setPassword("secret123");
        dto.setConfirmPassword("different");

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsWhenEmailExists() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setName("Jane");
        dto.setEmail("jane@example.com");
        dto.setPassword("secret123");
        dto.setConfirmPassword("secret123");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.register(dto));
    }
}
