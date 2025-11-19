# Hotel Booking System

Human-friendly Spring Boot 3 app for browsing rooms, booking stays, and managing inventory with an admin dashboard.

## Stack
- Java 17, Spring Boot 3.x (MVC, Data JPA, Validation)
- Spring Security + JWT (HttpOnly cookie)
- Thymeleaf views, custom CSS/JS
- H2 for dev; MySQL/PostgreSQL ready for prod

## Project Structure
```
src/main/java/com/hotelbooking
├── HotelBookingSystemApplication.java
├── config/        # security, JWT, web config, seeding
├── controller/    # public/auth, rooms, booking, admin
├── dto/           # BookingRequest, RoomRequest, RoomSearchRequest, UserRegistrationDto
├── exception/     # global handler + custom errors
├── model/         # User, Room, Booking, enums (Role, RoomType, BookingStatus)
├── repository/    # Spring Data JPA repos
├── service/       # interfaces + impl/
└── ...

src/main/resources
├── templates/
│   ├── home.html, login.html, register.html
│   ├── rooms/room-list.html, rooms/room-details.html
│   ├── booking/booking-history.html, booking/booking-confirm.html
│   ├── admin/admin-dashboard.html, admin/room-list.html, admin/add-room.html, admin/edit-room.html, admin/all-bookings.html
│   └── fragments/header.html, footer.html, toast.html
└── static/
    ├── css/style.css
    ├── js/app.js + auth helpers
    └── images/ (sample gallery + room photos)
```

## Run Locally
1) Prereqs: Java 17, Maven 3.9+.  
2) (Optional) Set DB creds + JWT secret:
```bash
export DB_URL=jdbc:mysql://localhost:3306/hotel_booking?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
export DB_USERNAME=root
export DB_PASSWORD=secret
export JWT_SECRET_KEY=Base64Encoded32BytesSecretHere
```
3) Build & start:
```bash
mvn clean package
java -jar target/hotel-booking-0.0.1-SNAPSHOT.jar
```
4) Open: http://localhost:9090  
   - Default admin (seed): `admin3112sand@gmail.com / nextnext`

Uploads land in `uploads/` (configurable via `app.upload-dir`). Public URL prefix: `/uploads/**`.

## Testing
```bash
mvn test
```
Note: some legacy tests may need updates if APIs change (e.g., JWT/booking signatures).

## Deployment Quick Notes
- Set env vars: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_DRIVER` (`com.mysql.cj.jdbc.Driver` or `org.postgresql.Driver`), `JWT_SECRET_KEY`, `PORT`, `app.upload-dir`.
- Build: `mvn clean package`
- Run: `java -jar target/hotel-booking-0.0.1-SNAPSHOT.jar`
- Mount a persistent volume for `uploads/` in production.

## Suggested Content Tweaks
- Replace sample images in `static/images/` with your own property photos.
- Update `data.sql` or seeder with real room inventory.
- Keep comments minimal and meaningful; remove unused files/folders.
