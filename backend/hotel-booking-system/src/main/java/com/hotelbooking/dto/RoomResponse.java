package com.hotelbooking.dto;

import com.hotelbooking.enums.RoomStatus;

/**
 * DTO — response room.
 */
public record RoomResponse(
        String id,
        String roomTypeId,
        String roomTypeName,
        String roomNumber,
        int floorNumber,
        RoomStatus status) {
}
