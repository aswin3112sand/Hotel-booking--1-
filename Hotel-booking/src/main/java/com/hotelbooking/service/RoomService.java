package com.hotelbooking.service;

import com.hotelbooking.dto.RoomRequest;
import com.hotelbooking.dto.RoomSearchRequest;
import com.hotelbooking.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {
    Page<Room> searchRooms(RoomSearchRequest criteria, Pageable pageable);
    Room getRoom(Long id);
    Room createRoom(RoomRequest request, MultipartFile imageFile);
    Room updateRoom(Long id, RoomRequest request, MultipartFile imageFile);
    void deleteRoom(Long id);
    List<Room> featuredRooms(int limit);
    List<Room> latestRooms(int limit);
    long countRooms();
}

