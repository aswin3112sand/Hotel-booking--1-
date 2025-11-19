package com.hotelbooking.dto;

import com.hotelbooking.model.RoomType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomSearchRequest {
    private String search;
    private String city;
    private RoomType roomType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    public void normalize() {
        if (search != null) {
            search = search.trim();
        }
        if (city != null) {
            city = city.trim();
        }
        if (minPrice != null && minPrice.signum() < 0) {
            minPrice = BigDecimal.ZERO;
        }
        if (minPrice != null && maxPrice != null && maxPrice.compareTo(minPrice) < 0) {
            BigDecimal tmp = minPrice;
            minPrice = maxPrice;
            maxPrice = tmp;
        }
    }
}

