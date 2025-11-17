package com.example.hotel.service;

import com.example.hotel.entity.Booking;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.User;
import com.example.hotel.repo.BookingRepository;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateBooking_Success() {
        User user = User.builder().id(1L).email("user@example.com").build();
        Room room = Room.builder().id(1L).title("Test Room").available(true).build();
        Booking booking = Booking.builder()
                .user(user)
                .room(room)
                .checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3))
                .guests(2)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        Booking result = bookingService.createBooking(1L, 1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 2);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(room, result.getRoom());
    }

    @Test
    void testCreateBooking_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            bookingService.createBooking(1L, 1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 2));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testCreateBooking_RoomNotFound() {
        User user = User.builder().id(1L).email("user@example.com").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            bookingService.createBooking(1L, 1L, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), 2));

        assertEquals("Room not found", exception.getMessage());
    }
}
