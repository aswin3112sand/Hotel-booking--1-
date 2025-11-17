package com.example.hotel.service;

import com.example.hotel.dto.RoomRequest;
import com.example.hotel.dto.RoomSearchRequest;
import com.example.hotel.entity.Room;
import com.example.hotel.entity.RoomType;
import com.example.hotel.repo.RoomRepository;
import com.example.hotel.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private StorageService storageService;

    @InjectMocks
    private RoomServiceImpl roomService;

    private RoomRequest request;

    @BeforeEach
    void setUp() {
        request = new RoomRequest();
        request.setTitle("Azure Suite");
        request.setDescription("Glass suite with skyline view");
        request.setRoomType(RoomType.SUITE);
        request.setPrice(new BigDecimal("15000"));
        request.setCity("Mumbai");
        request.setLocation("Nariman Point");
        request.setMaxGuests(3);
        request.setAmenities("Jacuzzi, Breakfast");
        request.setFeatured(true);
        request.setAvailable(true);
    }

    @Test
    void searchRooms_normalizesInput() {
        RoomSearchRequest filter = new RoomSearchRequest();
        filter.setCity("  goa  ");
        when(roomRepository.search(any(), any(), any(), any(), any(Pageable.class))).thenReturn(Page.empty());

        roomService.searchRooms(filter, Pageable.unpaged());

        verify(roomRepository).search(eq("goa"), isNull(), isNull(), isNull(), eq(Pageable.unpaged()));
    }

    @Test
    void createRoom_storesImageAndSaves() {
        MockMultipartFile file = new MockMultipartFile("image", "room.jpg", "image/jpeg", "bytes".getBytes());
        when(storageService.store(file)).thenReturn("room.jpg");
        when(roomRepository.save(any(Room.class))).thenAnswer(inv -> inv.getArgument(0));

        Room saved = roomService.createRoom(request, file);

        assertThat(saved.getImagePath()).isEqualTo("room.jpg");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void deleteRoom_removesImageAndEntity() {
        Room room = Room.builder().id(9L).imagePath("old.jpg").build();
        when(roomRepository.findById(9L)).thenReturn(java.util.Optional.of(room));

        roomService.deleteRoom(9L);

        verify(storageService).delete("old.jpg");
        verify(roomRepository).delete(room);
    }
}
