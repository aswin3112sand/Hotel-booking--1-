package com.example.hotel.dto;

import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomRequest {

    @NotBlank
    @Size(min = 4, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String description;

    @NotNull
    private RoomType roomType;

    @NotNull
    @DecimalMin("500.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    @NotBlank
    private String city;

    @NotBlank
    private String location;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer maxGuests;

    @Size(max = 320)
    private String amenities;

    @Size(max = 255)
    private String imageUrl;

    private boolean featured = true;
    private boolean available = true;

    public Room apply(Room room) {
        room.setTitle(title);
        room.setDescription(description);
        room.setRoomType(roomType);
        room.setPrice(price);
        room.setCity(city);
        room.setLocation(location);
        room.setMaxGuests(maxGuests);
        room.setAmenities(amenities);
        room.setImageUrl(imageUrl);
        room.setFeatured(featured);
        room.setAvailable(available);
        return room;
    }
}
