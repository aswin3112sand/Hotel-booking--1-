package com.example.hotel.controller;

import com.example.hotel.entity.RoomType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("roomTypes")
    public RoomType[] roomTypes() {
        return RoomType.values();
    }
}

