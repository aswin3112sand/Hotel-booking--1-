package com.example.hotel.service.impl;

import com.example.hotel.dto.BookingRequest;
import com.example.hotel.entity.*;
import com.example.hotel.exception.ResourceNotFoundException;
import com.example.hotel.repo.BookingRepository;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.repo.UserRepository;
import com.example.hotel.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(String email, BookingRequest request) {
        if (request.getCheckIn() == null || request.getCheckOut() == null || !request.getCheckOut().isAfter(request.getCheckIn())) {
            throw new IllegalArgumentException("Check-out must be after check-in");
        }
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!isRoomAvailable(room.getId(), request.getCheckIn(), request.getCheckOut())) {
            throw new IllegalStateException("Room unavailable for selected dates");
        }
        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        if (nights <= 0) {
            throw new IllegalArgumentException("Stay must be at least 1 night");
        }

        BigDecimal total = room.getPrice().multiply(BigDecimal.valueOf(nights));

        Booking booking = Booking.builder()
                .room(room)
                .user(user)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guests(request.getGuests())
                .totalPrice(total)
                .status(BookingStatus.CONFIRMED)
                .build();
        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getBookingsForUser(String email) {
        return bookingRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    @Override
    @Transactional
    public void cancelBooking(String email, Long bookingId) {
        Booking booking = bookingRepository.findByIdAndUserEmail(bookingId, email)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        if (!LocalDate.now().isBefore(booking.getCheckIn())) {
            throw new IllegalStateException("Cannot cancel on or after check-in date");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public long countActiveBookings() {
        return bookingRepository.countByStatus(BookingStatus.CONFIRMED)
                + bookingRepository.countByStatus(BookingStatus.PENDING);
    }

    @Override
    public long countConfirmedBookings() {
        return bookingRepository.countByStatus(BookingStatus.CONFIRMED);
    }

    @Override
    public BigDecimal totalRevenue() {
        BigDecimal total = bookingRepository.totalConfirmedRevenue();
        return total == null ? BigDecimal.ZERO : total;
    }

    @Override
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        if (roomId == null || checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            return false;
        }
        return bookingRepository.countOverlaps(roomId, checkIn, checkOut) == 0;
    }
}

