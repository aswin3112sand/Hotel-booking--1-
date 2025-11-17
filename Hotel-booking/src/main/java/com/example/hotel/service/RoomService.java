package com.example.hotel.service;

import com.example.hotel.dto.RoomRequest;
import com.example.hotel.dto.RoomSearchRequest;
import com.example.hotel.entity.Room;
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

