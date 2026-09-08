package com.hotelbooking.dto;

import com.hotelbooking.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO — response booking.
 */
public record BookingResponse(
        String id,
        BookingStatus status,
        List<BookingItemResponse> items,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalAmount,
        LocalDateTime expiredAt,
        LocalDateTime createdAt) {
}
