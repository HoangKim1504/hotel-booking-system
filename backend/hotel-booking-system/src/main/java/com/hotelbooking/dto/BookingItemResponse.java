package com.hotelbooking.dto;

import java.math.BigDecimal;

/**
 * DTO — response booking item.
 */
public record BookingItemResponse(
        String id,
        String roomTypeId,
        String roomTypeName,
        Integer quantity,
        BigDecimal price) {
}
