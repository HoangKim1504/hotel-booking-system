package com.hotelbooking.dto.booking;

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
