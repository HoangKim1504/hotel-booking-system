package com.hotelbooking.dto;

import java.math.BigDecimal;

/**
 * DTO — response room type.
 */
public record RoomTypeResponse(
        String id,
        String roomTypeName,
        Double roomSize,
        String facility,
        Integer maximumPeople,
        BigDecimal price) {
}
