package com.hotelbooking.service;

import com.hotelbooking.model.*;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.PaymentRepository;
import com.hotelbooking.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Booking booking;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(5L)
                .email("user@example.com")
                .name("User")
                .role(Role.USER)
                .build();
        booking = Booking.builder()
                .id(10L)
                .user(user)
                .room(Room.builder().id(1L).title("Room").price(new BigDecimal("1200")).build())
                .checkIn(java.time.LocalDate.now().plusDays(1))
                .checkOut(java.time.LocalDate.now().plusDays(3))
                .totalPrice(new BigDecimal("2400"))
                .status(BookingStatus.PENDING)
                .build();
    }

    @Test
    void ensurePayment_createsPendingPayment() {
        given(bookingRepository.findById(10L)).willReturn(Optional.of(booking));
        given(paymentRepository.findByBookingId(10L)).willReturn(Optional.empty());
        given(paymentRepository.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.ensurePayment(10L, "user@example.com");

        assertThat(payment.getBooking()).isEqualTo(booking);
        assertThat(payment.getAmount()).isEqualByComparingTo("2400");
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(payment.getTransactionRef()).isNotBlank();
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void markSuccess_setsPaymentAndBookingStatuses() {
        Payment existing = Payment.builder()
                .id(3L)
                .booking(booking)
                .amount(booking.getTotalPrice())
                .status(PaymentStatus.PENDING)
                .build();
        given(paymentRepository.findByIdAndBookingUserEmail(3L, "user@example.com"))
                .willReturn(Optional.of(existing));
        given(paymentRepository.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        Payment payment = paymentService.markSuccess(3L, "user@example.com", PaymentMethod.CARD);

        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getMethod()).isEqualTo(PaymentMethod.CARD);
        assertThat(payment.getPaidAt()).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(payment.getTransactionRef()).isNotBlank();
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    void ensurePayment_rejectsWhenUserMismatch() {
        given(bookingRepository.findById(10L)).willReturn(Optional.of(booking));

        assertThrows(IllegalStateException.class,
                () -> paymentService.ensurePayment(10L, "other@example.com"));
    }
}
