package com.hotelbooking.dto.booking;

import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO — response booking.
 */
public record SimpleBookingResponse(
        String bookingId,
        String userId,
        String username,
        String fullName,
        BookingStatus bookingStatus,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BigDecimal totalAmount,
        PaymentStatus paymentStatus,
        String paymentMethod,
        LocalDateTime expiredAt,
        LocalDateTime createdAt) {
}
