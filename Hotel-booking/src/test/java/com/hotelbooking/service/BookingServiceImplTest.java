package com.hotelbooking.service;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.UserRepository;
import com.hotelbooking.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private Room room;
    private User user;
    private BookingRequest request;

    @BeforeEach
    void init() {
        room = Room.builder()
                .id(1L)
                .title("Azure")
                .price(new BigDecimal("10000"))
                .roomType(RoomType.SUITE)
                .city("Goa")
                .location("Beach")
                .description("desc")
                .maxGuests(2)
                .build();
        user = User.builder().id(2L).email("user@example.com").role(Role.USER).password("pw").name("User").build();
        request = new BookingRequest();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.now().plusDays(2));
        request.setCheckOut(LocalDate.now().plusDays(5));
        request.setGuests(2);
    }

    @Test
    void createBooking_persistsBookingWithTotal() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bookingRepository.countOverlaps(anyLong(), any(), any())).thenReturn(0L);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking booking = bookingService.createBooking("user@example.com", request);

        assertThat(booking.getTotalPrice()).isEqualByComparingTo("30000");
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void createBooking_throwsWhenDatesInvalid() {
        request.setCheckOut(request.getCheckIn());
        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking("user@example.com", request));
    }

    @Test
    void createBooking_throwsWhenUnavailable() {
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(bookingRepository.countOverlaps(anyLong(), any(), any())).thenReturn(1L);

        assertThrows(IllegalStateException.class, () -> bookingService.createBooking("user@example.com", request));
    }

    @Test
    void cancelBooking_updatesStatus() {
        Booking booking = Booking.builder()
                .id(9L)
                .user(user)
                .room(room)
                .checkIn(LocalDate.now().plusDays(3))
                .checkOut(LocalDate.now().plusDays(5))
                .status(BookingStatus.CONFIRMED)
                .build();
        when(bookingRepository.findByIdAndUserEmail(9L, "user@example.com")).thenReturn(Optional.of(booking));

        bookingService.cancelBooking("user@example.com", 9L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        verify(bookingRepository).save(booking);
    }
}
