package com.hotelbooking.dto.booking;

import com.hotelbooking.enums.BookingStatus;

/**
 * DTO — response update booking.
 */
public record UpdateBookingResponse(
        String id,
        BookingStatus status) {
}