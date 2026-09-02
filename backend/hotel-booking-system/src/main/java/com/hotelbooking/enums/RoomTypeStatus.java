package com.hotelbooking.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RoomTypeStatus {
    ACTIVE,
    INACTIVE;

    @JsonCreator
    public static RoomTypeStatus fromValue(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return RoomTypeStatus.valueOf(value.trim().toUpperCase());
    }
}