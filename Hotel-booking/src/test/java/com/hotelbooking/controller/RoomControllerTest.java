package com.hotelbooking.controller;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.dto.RoomSearchRequest;
import com.hotelbooking.model.Room;
import com.hotelbooking.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @InjectMocks
    private RoomController roomController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
    }

    @Test
    void testListRooms() throws Exception {
        Room room1 = Room.builder()
                .id(1L)
                .title("Deluxe Room")
                .price(BigDecimal.valueOf(150))
                .build();
        Room room2 = Room.builder()
                .id(2L)
                .title("Standard Room")
                .price(BigDecimal.valueOf(100))
                .build();

        List<Room> rooms = Arrays.asList(room1, room2);
        Page<Room> roomPage = new PageImpl<>(rooms, PageRequest.of(0, 9), 2);

        when(roomService.searchRooms(any(RoomSearchRequest.class), any(Pageable.class))).thenReturn(roomPage);

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/room-list"))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    void testRoomDetails() throws Exception {
        Room room = Room.builder()
                .id(1L)
                .title("Deluxe Room")
                .price(BigDecimal.valueOf(150))
                .build();

        when(roomService.getRoom(1L)).thenReturn(room);

        mockMvc.perform(get("/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/room-details"))
                .andExpect(model().attributeExists("room"))
                .andExpect(model().attributeExists("bookingRequest"));
    }
}
