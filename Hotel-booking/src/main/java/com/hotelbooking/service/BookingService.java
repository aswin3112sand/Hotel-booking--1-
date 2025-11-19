package com.hotelbooking.service;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.model.Booking;

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

