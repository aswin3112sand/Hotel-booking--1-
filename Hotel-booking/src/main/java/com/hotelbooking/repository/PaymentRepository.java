package com.hotelbooking.repository;

import com.hotelbooking.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);
    Optional<Payment> findByIdAndBookingUserEmail(Long id, String email);
}
