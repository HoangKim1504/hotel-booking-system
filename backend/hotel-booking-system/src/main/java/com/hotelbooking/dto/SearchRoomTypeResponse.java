package com.hotelbooking.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * DTO — response search room type.
 */
@Builder
public record SearchRoomTypeResponse(
        String id,
        String roomTypeName,
        Double roomSize,
        String facility,
        Integer maximumPeople,
        BigDecimal price,
        Integer availableQuantity) {
}