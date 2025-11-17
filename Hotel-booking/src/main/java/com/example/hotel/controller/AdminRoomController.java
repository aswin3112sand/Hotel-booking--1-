package com.example.hotel.controller;

import com.example.hotel.dto.RoomRequest;
import com.example.hotel.dto.RoomSearchRequest;
import com.example.hotel.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rooms")
public class AdminRoomController {

    private final RoomService roomService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(value = "msg", required = false) String msg,
                       @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("rooms", roomService.searchRooms(new RoomSearchRequest(), Pageable.unpaged()).getContent());
        model.addAttribute("msg", msg);
        model.addAttribute("error", error);
        return "admin/room-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("room", new RoomRequest());
        return "admin/add-room";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("room") RoomRequest request,
                         BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/add-room";
        }
        try {
            roomService.createRoom(request, imageFile);
            redirectAttributes.addFlashAttribute("msg", "Room created");
            return "redirect:/admin/rooms";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/admin/rooms/new";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        var room = roomService.getRoom(id);
        RoomRequest request = new RoomRequest();
        request.setTitle(room.getTitle());
        request.setDescription(room.getDescription());
        request.setRoomType(room.getRoomType());
        request.setPrice(room.getPrice());
        request.setCity(room.getCity());
        request.setLocation(room.getLocation());
        request.setMaxGuests(room.getMaxGuests());
        request.setAmenities(room.getAmenities());
        request.setImageUrl(room.getImageUrl());
        request.setFeatured(room.isFeatured());
        request.setAvailable(room.isAvailable());
        model.addAttribute("room", request);
        model.addAttribute("roomId", id);
        model.addAttribute("currentImage", room.getImagePath());
        return "admin/edit-room";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("room") RoomRequest request,
                         BindingResult result,
                         @RequestParam("imageFile") MultipartFile imageFile,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid room data");
            return "redirect:/admin/rooms/" + id + "/edit";
        }
        try {
            roomService.updateRoom(id, request, imageFile);
            redirectAttributes.addFlashAttribute("msg", "Room updated");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/rooms";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            roomService.deleteRoom(id);
            redirectAttributes.addFlashAttribute("msg", "Room deleted");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/rooms";
    }
}
