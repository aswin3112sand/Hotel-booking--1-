package com.hotelbooking.controller;

import com.hotelbooking.dto.BookingRequest;
import com.hotelbooking.dto.RoomSearchRequest;
import com.hotelbooking.model.Room;
import com.hotelbooking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public String list(@ModelAttribute("filter") RoomSearchRequest filter,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "9") int size,
                       Model model) {
        if (filter == null) {
            filter = new RoomSearchRequest();
        }
        if (size < 3) size = 3;
        if (size > 12) size = 12;
        Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by("price").ascending());
        Page<Room> rooms = roomService.searchRooms(filter, pageable);
        model.addAttribute("rooms", rooms.getContent());
        model.addAttribute("page", rooms);
        return "rooms/room-list";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id,
                          Model model) {
        var room = roomService.getRoom(id);
        BookingRequest request = new BookingRequest();
        request.setRoomId(room.getId());
        model.addAttribute("room", room);
        model.addAttribute("bookingRequest", request);
        return "rooms/room-details";
    }
}
