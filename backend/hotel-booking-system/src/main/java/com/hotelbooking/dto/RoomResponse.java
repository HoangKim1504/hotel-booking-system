package com.hotelbooking.dto;

import com.hotelbooking.enums.RoomStatus;

/**
 * DTO — response room.
 */
public record RoomResponse(
        String id,
        String roomTypeId,
        String roomTypeName,
        int roomNumber,
        int floorNumber,
        RoomStatus status) {
}
