package com.hotelbooking.dto;

import com.hotelbooking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO — response booking.
 */
public record SimpleBookingResponse(
        String id,
        BookingStatus status,
        BigDecimal totalAmount,
        LocalDateTime expiredAt,
        LocalDateTime createdAt) {
}
