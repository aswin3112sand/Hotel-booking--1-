package com.hotelbooking.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class BookingRequest {

    @NotNull
    private Long roomId;

    @NotNull
    @FutureOrPresent
    private LocalDate checkIn;

    @NotNull
    @Future
    private LocalDate checkOut;

    @Min(1)
    @Max(8)
    private int guests = 1;

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public int getGuests() {
        return guests;
    }

    public void setGuests(int guests) {
        this.guests = guests;
    }
}
