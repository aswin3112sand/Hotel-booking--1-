package com.hotelbooking.model;

public enum RoomType {
    STANDARD("Standard Room"),
    DELUXE("Deluxe Room"),
    SUITE("Royal Suite"),
    FAMILY("Family Room"),
    PENTHOUSE("Skyline Penthouse");

    private final String label;

    RoomType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

