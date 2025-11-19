package com.hotelbooking.controller;

import com.hotelbooking.model.RoomType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("roomTypes")
    public RoomType[] roomTypes() {
        return RoomType.values();
    }
}

