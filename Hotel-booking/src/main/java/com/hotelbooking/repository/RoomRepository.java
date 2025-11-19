package com.hotelbooking.repository;

import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
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
        where (:search is null or :search = '' or lower(r.title) like lower(concat('%', :search, '%')) or lower(r.city) like lower(concat('%', :search, '%')))
          and (:city is null or :city = '' or lower(r.city) like lower(concat('%', :city, '%')))
          and (:type is null or r.roomType = :type)
          and (:minPrice is null or r.price >= :minPrice)
          and (:maxPrice is null or r.price <= :maxPrice)
    """)
    Page<Room> search(@Param("search") String search,
                      @Param("city") String city,
                      @Param("type") RoomType type,
                      @Param("minPrice") BigDecimal minPrice,
                      @Param("maxPrice") BigDecimal maxPrice,
                      Pageable pageable);

    List<Room> findTop6ByFeaturedTrueOrderByUpdatedAtDesc();

    List<Room> findTop4ByAvailableTrueOrderByCreatedAtDesc();

    long countByAvailableTrue();
}
