package com.example.hotel.controller;

import com.example.hotel.dto.RoomSearchRequest;
import com.example.hotel.config.JwtUtil;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.service.DbUserDetailsService;
import com.example.hotel.service.RoomService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@AutoConfigureMockMvc(addFilters = false)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private DbUserDetailsService dbUserDetailsService;

    @Test
    void list_returnsRoomsView() throws Exception {
        Room room = Room.builder()
                .id(1L)
                .title("Skyline Suite")
                .description("desc")
                .roomType(RoomType.SUITE)
                .price(new BigDecimal("10000"))
                .city("Goa")
                .location("Beach")
                .maxGuests(2)
                .build();
        Page<Room> page = new PageImpl<>(List.of(room), PageRequest.of(0, 9), 1);
        Mockito.when(roomService.searchRooms(any(RoomSearchRequest.class), any())).thenReturn(page);

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms"))
                .andExpect(model().attributeExists("rooms"));
    }

    @Test
    void details_loadsRoom() throws Exception {
        Room room = Room.builder()
                .id(2L)
                .title("Lagoon")
                .description("desc")
                .roomType(RoomType.DELUXE)
                .price(new BigDecimal("8000"))
                .city("Goa")
                .location("Candolim")
                .maxGuests(2)
                .build();
        Mockito.when(roomService.getRoom(2L)).thenReturn(room);

        mockMvc.perform(get("/rooms/2"))
                .andExpect(status().isOk())
                .andExpect(view().name("room-details"))
                .andExpect(model().attributeExists("bookingRequest"));
    }
}
