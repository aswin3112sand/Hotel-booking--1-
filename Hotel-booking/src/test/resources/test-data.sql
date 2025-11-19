-- Seed users
INSERT INTO users (id, email, name, password, role)
VALUES (1, 'user@example.com', 'Test User', 'pw', 'USER');

-- Seed rooms
INSERT INTO rooms (id, title, description, room_type, price, city, location, amenities, max_guests, image_path, image_url, available, featured, created_at, updated_at)
VALUES (1,
        'Ocean View Suite',
        'Spacious suite with sea-facing balcony and complimentary breakfast.',
        'SUITE',
        1500.00,
        'Goa',
        'Beach Road',
        'WiFi,Pool',
        2,
        NULL,
        NULL,
        TRUE,
        TRUE,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);

-- Seed bookings (room 1 from 2024-12-10 to 2024-12-12)
INSERT INTO bookings (id, user_id, room_id, check_in, check_out, guests, status, total_price, created_at, updated_at)
VALUES (1,
        1,
        1,
        DATE '2024-12-10',
        DATE '2024-12-12',
        2,
        'CONFIRMED',
        3000.00,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP);
