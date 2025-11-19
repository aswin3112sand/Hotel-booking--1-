package com.hotelbooking.service.impl;

import com.hotelbooking.dto.RoomRequest;
import com.hotelbooking.dto.RoomSearchRequest;
import com.hotelbooking.model.Room;
import com.hotelbooking.exception.ResourceNotFoundException;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.service.RoomService;
import com.hotelbooking.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final StorageService storageService;

    @Override
    public Page<Room> searchRooms(RoomSearchRequest criteria, Pageable pageable) {
        if (criteria != null) {
            criteria.normalize();
        }
        if (pageable == null) {
            pageable = org.springframework.data.domain.PageRequest.of(0, 20);
        }
        return roomRepository.search(
                criteria != null ? criteria.getSearch() : null,
                criteria != null ? criteria.getCity() : null,
                criteria != null ? criteria.getRoomType() : null,
                criteria != null ? criteria.getMinPrice() : null,
                criteria != null ? criteria.getMaxPrice() : null,
                pageable
        );
    }

    @Override
    public Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @Override
    @Transactional
    public Room createRoom(RoomRequest request, MultipartFile imageFile) {
        Room room = request.apply(new Room());
        if (imageFile != null && !imageFile.isEmpty()) {
            room.setImagePath(storageService.store(imageFile));
        }
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public Room updateRoom(Long id, RoomRequest request, MultipartFile imageFile) {
        Room room = getRoom(id);
        String previousPath = room.getImagePath();
        request.apply(room);

        if (imageFile != null && !imageFile.isEmpty()) {
            room.setImagePath(storageService.store(imageFile));
            storageService.delete(previousPath);
        }
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoom(id);
        storageService.delete(room.getImagePath());
        roomRepository.delete(room);
    }

    @Override
    public List<Room> featuredRooms(int limit) {
        return roomRepository.findTop6ByFeaturedTrueOrderByUpdatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    @Override
    public List<Room> latestRooms(int limit) {
        return roomRepository.findTop4ByAvailableTrueOrderByCreatedAtDesc()
                .stream()
                .limit(limit)
                .toList();
    }

    @Override
    public long countRooms() {
        return roomRepository.count();
    }
}
