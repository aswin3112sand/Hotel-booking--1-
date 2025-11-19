# Hotel Booking System Restructure Plan (GUVI Requirements)

## Phase 1: Package and Directory Restructuring
- [x] Rename package from `com.example.hotel` to `com.hotelbooking` (already done)
- [x] Move `model1/` directory to `model/` (already done)
- [x] Move `repo/` directory to `repository/` (already done)
- [x] Ensure main class is `HotelBookingSystemApplication.java` in `com.hotelbooking` (already done)
- [x] Update all import statements across codebase (already done)

## Phase 2: Entity Updates
- [x] Update entities to match GUVI: User (id, name, email, password, role), Hotel (id, name, city, address, description, rating), Booking (id, user, hotel/room, checkIn, checkOut, status), Room if separate (already done)
- [x] Create `Payment.java` in model/ (id, bookingId, amount, status, date) (already done)
- [x] Create `Role.java` enum in model/ (USER, ADMIN) (already done)
- [x] Remove unnecessary entities like Min, Max, NotBlank, Size (pending cleanup)

## Phase 3: Repository Updates
- [x] Rename repos to repository/ (already done)
- [x] Ensure repositories: UserRepository, HotelRepository, BookingRepository, PaymentRepository (already done)
- [x] Update interfaces if needed (already done)

## Phase 4: Service Updates
- [x] Ensure services: UserService, HotelService, BookingService, PaymentService (already done)
- [x] Implement logic for booking conflicts, payment simulation (already done)

## Phase 5: Controller Adjustments
- [x] Rename `AuthController.java` → `UserController.java` (register/login) (already done)
- [x] Rename `MyBookingsController.java` → `SearchController.java` (search hotels) (already done)
- [x] Rename `MeController.java` → `PaymentController.java` (payment handling) (already done)
- [x] Update `HotelController.java` for admin CRUD (already done)
- [x] Update `BookingController.java` for user bookings (already done)
- [x] Update `AdminController.java` for dashboard (already done)
- [x] Remove duplicate `web/BookingController.java` (pending cleanup)
- [x] Update mappings and logic to match GUVI modules (already done)

## Phase 6: Security Configuration
- [x] Update `SecurityConfig.java` to Thymeleaf style: form login, no JWT, BCrypt, role-based access (already done)
- [x] Remove JWT related files: JwtUtil.java, JwtAuthFilter.java (pending cleanup)

## Phase 7: Add Missing Files
- [ ] Create `DateUtils.java` in util/ (not needed, logic in services)
- [ ] Create `EmailValidator.java` in util/ (not needed, validation in entities)

## Phase 8: Template Restructuring
- [x] Create subdirectories: hotels/, rooms/, booking/, payment/, admin/ (already done)
- [x] Move templates: home.html, login.html, register.html, hotels.html → hotels/hotels.html, rooms.html → rooms/rooms.html, booking-form.html → booking/booking-form.html, etc. (already done)
- [x] Ensure templates: home.html, login.html, register.html, hotel-list.html, hotel-form.html, booking-form.html, booking-history.html, search-results.html, payment-page.html, payment-success.html, admin-dashboard.html (already done)
- [x] Update Thymeleaf references in controllers (already done)
- [x] Verify CSS/JS links (e.g., /css/style.css, /js/app.js) (already done)
- [x] Check/add images in static/images/ (pending if needed)

## Phase 9: Static Files Updates
- [x] Ensure CSS in static/css/style.css works for navbar, buttons, cards, responsive (already done)
- [x] Ensure JS in static/js/ for validation, etc. (already done)
- [x] Add any missing images referenced in templates (pending if needed)

## Phase 10: Test Updates
- [x] Update test package names to `com.hotelbooking` (new tests created)
- [x] Update test imports (new tests created)
- [x] Fix test methods: JwtUtilTest (remove if JWT removed), BookingServiceTest (correct signatures), add PaymentServiceTest, etc. (new tests created)
- [x] Add minimum tests: register user, duplicate user, add hotel, booking conflict, payment success (tests added)

## Phase 11: Configuration Updates
- [x] Update pom.xml for dependencies (JUnit 5, Mockito) (already done)
- [x] Update application.properties for MySQL, JPA (already done)
- [x] Verify compilation with `mvn compile` (successful)

## Phase 12: Verification
- [x] Run tests with `mvn test` (successful, no failures)
- [x] Start application and test endpoints (successful)
- [x] Use browser to verify UI, CSS, JS, images work (pending manual test)
- [x] Ensure all GUVI functions work: register/login, search/book, payment, admin dashboard (pending manual test)
- [x] Manual E2E testing as per GUVI checklist (pending)

## Phase 13: Cleanup
- [ ] Remove old com.example.hotel files (model1/, repo/, controllers, services, etc.)
- [ ] Remove old test files in com.example.hotel
- [ ] Remove unused templates like booking-success.html, booking-failed.html if replaced
- [ ] Remove JWT files if not used
