package com.hotelbooking.controller;

import com.hotelbooking.model.*;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.DbUserDetailsService;
import com.hotelbooking.service.RoomService;
import com.hotelbooking.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminDashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;

    @MockBean
    private DbUserDetailsService dbUserDetailsService;

    @Test
    void dashboard_populatesAnalytics() throws Exception {
        Mockito.when(roomService.countRooms()).thenReturn(5L);
        Mockito.when(bookingService.countActiveBookings()).thenReturn(3L);
        Mockito.when(bookingService.countConfirmedBookings()).thenReturn(2L);
        Mockito.when(userService.countUsers()).thenReturn(8L);
        Mockito.when(bookingService.totalRevenue()).thenReturn(new BigDecimal("45000"));
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.CONFIRMED)
                .room(Room.builder().title("Azure").price(new BigDecimal("1000")).roomType(RoomType.SUITE).city("Goa").location("Beach").description("desc").maxGuests(2).build())
                .user(User.builder().email("guest@example.com").role(Role.USER).name("Guest").password("pw").build())
                .checkIn(LocalDate.now())
                .checkOut(LocalDate.now().plusDays(2))
                .totalPrice(new BigDecimal("2000"))
                .guests(2)
                .build();
        Mockito.when(bookingService.getAllBookings()).thenReturn(List.of(booking));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin-dashboard"))
                .andExpect(model().attributeExists("roomCount", "recentBookings", "revenue"));
    }
}
