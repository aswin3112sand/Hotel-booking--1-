package com.hotelbooking.service.impl;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.model.*;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Room room;
    private BookingRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .name("Test User")
                .build();

        room = Room.builder()
                .id(1L)
                .title("Deluxe Room")
                .price(BigDecimal.valueOf(150))
                .build();

        request = new BookingRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(1));
        request.setCheckOut(LocalDate.now().plusDays(3));
        request.setGuests(2);
    }

    @Test
    void testCreateBooking_Success() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.countOverlaps(1L, request.getCheckIn(), request.getCheckOut())).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Booking result = bookingService.createBooking("test@example.com", request);

        assertNotNull(result);
        assertEquals(user, result.getUser());
        assertEquals(room, result.getRoom());
        assertEquals(BigDecimal.valueOf(300), result.getTotalPrice()); // 150 * 2 nights
        assertEquals(BookingStatus.PENDING, result.getStatus());
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_UserNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            bookingService.createBooking("test@example.com", request));
    }

    @Test
    void testCreateBooking_RoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
            bookingService.createBooking("test@example.com", request));
    }

    @Test
    void testCreateBooking_RoomUnavailable() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.countOverlaps(1L, request.getCheckIn(), request.getCheckOut())).thenReturn(1L);

        assertThrows(IllegalStateException.class, () ->
            bookingService.createBooking("test@example.com", request));
    }

    @Test
    void testIsRoomAvailable_True() {
        when(bookingRepository.countOverlaps(1L, request.getCheckIn(), request.getCheckOut())).thenReturn(0L);

        boolean result = bookingService.isRoomAvailable(1L, request.getCheckIn(), request.getCheckOut());

        assertTrue(result);
    }

    @Test
    void testIsRoomAvailable_False() {
        when(bookingRepository.countOverlaps(1L, request.getCheckIn(), request.getCheckOut())).thenReturn(1L);

        boolean result = bookingService.isRoomAvailable(1L, request.getCheckIn(), request.getCheckOut());

        assertFalse(result);
    }
}
