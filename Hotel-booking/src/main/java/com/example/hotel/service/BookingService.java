package com.example.hotel.service;

import com.example.hotel.dto.BookingRequest;
import com.example.hotel.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    Booking createBooking(String email, BookingRequest request);
    List<Booking> getBookingsForUser(String email);
    void cancelBooking(String email, Long bookingId);
    List<Booking> getAllBookings();
    long countActiveBookings();
    long countConfirmedBookings();
    BigDecimal totalRevenue();
    boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut);
}

