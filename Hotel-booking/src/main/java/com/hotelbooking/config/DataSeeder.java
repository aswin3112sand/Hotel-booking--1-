package com.hotelbooking.config;

import com.hotelbooking.model.Role;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.model.User;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedData(UserRepository users,
                               RoomRepository rooms,
                               PasswordEncoder encoder) {
        return args -> {
            final String adminEmail = "admin3112sand@gmail.com";
            if (users.findByEmail(adminEmail).isEmpty()) {
                User admin = User.builder()
                        .name("System Admin")
                        .email(adminEmail)
                        .password(encoder.encode("nextnext"))
                        .role(Role.ADMIN)
                        .build();
                users.save(admin);
                log.info("Seeded default admin user: {} / {}", adminEmail, "nextnext");
            }

            if (rooms.count() == 0) {
                List<Room> seedRooms = List.of(
                        createRoom("Azure Crown Suite", "Experience panoramic skyline views with a private butler and marble bathroom.",
                                RoomType.SUITE, new BigDecimal("18999"), "Mumbai", "Nariman Point", 3,
                                "Private butler, Jacuzzi, WiFi, Breakfast", "https://images.unsplash.com/photo-1501117716987-c8e1ecb210cc"),
                        createRoom("Laguna Spa Retreat", "Beachfront luxury suite with spa-inspired ensuite and curated minibar.",
                                RoomType.DELUXE, new BigDecimal("12999"), "Goa", "Candolim Beachfront", 2,
                                "Spa ensuite, Infinity pool, WiFi, Gourmet breakfast", "https://images.unsplash.com/photo-1505691723518-36a5ac3be353"),
                        createRoom("Nordic Skylight Loft", "Glass-roof loft with floating bed and immersive lighting system.",
                                RoomType.PENTHOUSE, new BigDecimal("21999"), "Manali", "Himalayan Ridge Road", 4,
                                "Fireplace, Heated floors, WiFi, Mountain concierge", "https://images.unsplash.com/photo-1505691938895-1758d7feb511"),
                        createRoom("Emerald Family Residence", "Spacious suite designed for families with indoor play nook.",
                                RoomType.FAMILY, new BigDecimal("9999"), "Jaipur", "Amber Palace Lane", 5,
                                "Play nook, Chef on call, WiFi", "/images/3.jpg"),
                        createRoom("Cobalt Business Class", "Elegant city room for business travellers with ergonomic workspace.",
                                RoomType.STANDARD, new BigDecimal("7499"), "Bengaluru", "UB City Plaza", 2,
                                "Work desk, WiFi 1Gbps, Espresso bar", "/images/4.jpg")
                );
                rooms.saveAll(seedRooms);
                log.info("Seeded {} sample rooms", seedRooms.size());
            }
        };
    }

    private Room createRoom(String title,
                            String description,
                            RoomType type,
                            BigDecimal price,
                            String city,
                            String location,
                            int guests,
                            String amenities,
                            String imageUrl) {
        return Room.builder()
                .title(title)
                .description(description)
                .roomType(type)
                .price(price)
                .city(city)
                .location(location)
                .maxGuests(guests)
                .amenities(amenities)
                .imageUrl(imageUrl)
                .featured(true)
                .available(true)
                .build();
    }
}
