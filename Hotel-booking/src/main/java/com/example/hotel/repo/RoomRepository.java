package com.example.hotel.repo;

import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("""
        select r from Room r
        where (:city is null or :city = '' or lower(r.city) like lower(concat('%', :city, '%')))
          and (:type is null or r.roomType = :type)
          and (:minPrice is null or r.price >= :minPrice)
          and (:maxPrice is null or r.price <= :maxPrice)
    """)
    Page<Room> search(@Param("city") String city,
                      @Param("type") RoomType type,
                      @Param("minPrice") BigDecimal minPrice,
                      @Param("maxPrice") BigDecimal maxPrice,
                      Pageable pageable);

    List<Room> findTop6ByFeaturedTrueOrderByUpdatedAtDesc();

    List<Room> findTop4ByAvailableTrueOrderByCreatedAtDesc();

    long countByAvailableTrue();
}
