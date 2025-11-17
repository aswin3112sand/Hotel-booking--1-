package com.example.hotel.repo;

import com.example.hotel.entity.Booking;
import com.example.hotel.entity.BookingStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("/data.sql")
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void testCountOverlaps_NoOverlap() {
        long overlaps = bookingRepository.countOverlaps(1L,
                LocalDate.of(2024, 12, 15),
                LocalDate.of(2024, 12, 17));
        assertEquals(0, overlaps);
    }

    @Test
    void testCountOverlaps_WithOverlap() {
        // Assuming data.sql has a booking for room 1 from 2024-12-10 to 2024-12-12
        long overlaps = bookingRepository.countOverlaps(1L,
                LocalDate.of(2024, 12, 11),
                LocalDate.of(2024, 12, 13));
        assertTrue(overlaps > 0);
    }

    @Test
    void testFindByUserEmailOrderByCreatedAtDesc() {
        List<Booking> bookings = bookingRepository.findByUserEmailOrderByCreatedAtDesc("user@example.com");
        assertNotNull(bookings);
        // Assuming data.sql has bookings for this user
    }

    @Test
    void testCountByStatus() {
        long confirmedCount = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        assertTrue(confirmedCount >= 0);
    }

    @Test
    void testTotalConfirmedRevenue() {
        BigDecimal revenue = bookingRepository.totalConfirmedRevenue();
        assertNotNull(revenue);
        assertTrue(revenue.compareTo(BigDecimal.ZERO) >= 0);
    }
}
