package com.example.hotel.repo;

import com.example.hotel.entity.Booking;
import com.example.hotel.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
      select count(b) from Booking b
      where b.room.id = :roomId
        and b.status <> 'CANCELLED'
        and (b.checkIn < :end and b.checkOut > :start)
    """)
    long countOverlaps(@Param("roomId") Long roomId,
                       @Param("start") LocalDate start,
                       @Param("end") LocalDate end);

    List<Booking> findByUserEmailOrderByCreatedAtDesc(String email);

    Optional<Booking> findByIdAndUserEmail(Long id, String email);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    @Query("select coalesce(sum(b.totalPrice),0) from Booking b where b.status = 'CONFIRMED'")
    BigDecimal totalConfirmedRevenue();
}
