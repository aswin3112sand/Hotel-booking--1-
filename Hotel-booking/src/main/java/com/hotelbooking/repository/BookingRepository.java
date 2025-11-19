package com.hotelbooking.repository;

import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingStatus;
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

    @Query("""
      select b from Booking b
      left join fetch b.room
      where b.user.email = :email
      order by b.createdAt desc
    """)
    List<Booking> findUserBookingsWithDetails(@Param("email") String email);

    Optional<Booking> findByIdAndUserEmail(Long id, String email);

    List<Booking> findAllByOrderByCreatedAtDesc();

    long countByStatus(BookingStatus status);

    @Query("select coalesce(sum(b.totalPrice),0) from Booking b where b.status = 'CONFIRMED'")
    BigDecimal totalConfirmedRevenue();
}
