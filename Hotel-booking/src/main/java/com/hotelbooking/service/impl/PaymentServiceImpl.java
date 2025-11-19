package com.hotelbooking.service.impl;

import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.PaymentRepository;
import com.hotelbooking.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Payment ensurePayment(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        validateOwnership(booking, userEmail);

        return paymentRepository.findByBookingId(bookingId)
                .map(existing -> {
                    if (existing.getStatus() == PaymentStatus.FAILED) {
                        existing.setStatus(PaymentStatus.PENDING);
                        existing.setPaidAt(null);
                        existing.setMethod(null);
                    }
                    return existing;
                })
                .orElseGet(() -> paymentRepository.save(
                        Payment.builder()
                                .booking(booking)
                                .amount(booking.getTotalPrice())
                                .status(PaymentStatus.PENDING)
                                .transactionRef(generateTransactionRef(bookingId))
                                .build()
                ));
    }

    @Override
    @Transactional
    public Payment markSuccess(Long paymentId, String userEmail, PaymentMethod method) {
        Payment payment = getPaymentForUser(paymentId, userEmail);
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return payment;
        }
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setMethod(method);
        payment.setPaidAt(LocalDateTime.now());
        if (payment.getTransactionRef() == null) {
            payment.setTransactionRef(generateTransactionRef(payment.getBooking().getId()));
        }
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }

    @Override
    @Transactional
    public Payment markFailure(Long paymentId, String userEmail, String reason) {
        Payment payment = getPaymentForUser(paymentId, userEmail);
        payment.setStatus(PaymentStatus.FAILED);
        if (payment.getTransactionRef() == null) {
            payment.setTransactionRef(generateTransactionRef(payment.getBooking().getId()));
        }
        Booking booking = payment.getBooking();
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentForUser(Long paymentId, String userEmail) {
        return paymentRepository.findByIdAndBookingUserEmail(paymentId, userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
    }

    private void validateOwnership(Booking booking, String email) {
        if (booking.getUser() == null || !booking.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new IllegalStateException("You can only pay for your own bookings");
        }
    }

    private String generateTransactionRef(Long bookingId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss", Locale.US));
        return "PAY-" + bookingId + "-" + timestamp;
    }
}
