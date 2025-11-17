package com.example.hotel.entity;

import jakarta.persistence.*;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 4, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomType roomType;

    @NotNull
    @DecimalMin(value = "500.00")
    @Digits(integer = 8, fraction = 2)
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotBlank
    @Size(max = 80)
    private String city;

    @NotBlank
    @Size(max = 160)
    private String location;

    @Size(max = 320)
    private String amenities;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer maxGuests;

    private String imagePath;
    private String imageUrl;

    @Builder.Default
    private boolean available = true;

    @Builder.Default
    private boolean featured = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    @Transient
    public String getDisplayImage() {
        if (imagePath != null && !imagePath.isBlank()) {
            return "/uploads/" + imagePath;
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            return imageUrl;
        }
        return "https://images.unsplash.com/photo-1501117716987-c8e1ecb210cc?auto=format&fit=crop&w=800&q=80";
    }

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
