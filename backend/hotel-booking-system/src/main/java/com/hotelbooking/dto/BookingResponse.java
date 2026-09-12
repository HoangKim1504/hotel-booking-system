package com.hotelbooking.dto;

import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO — response booking.
 */
public record BookingResponse(
        String id,
        BookingStatus bookingStatus,
        List<BookingItemResponse> items,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalAmount,
        PaymentStatus paymentStatus,
        String paymentMethod,
        LocalDateTime expiredAt,
        LocalDateTime createdAt) {
}
