package com.hotelbooking.dto;

import com.hotelbooking.enums.BookingStatus;

/**
 * DTO — response update booking.
 */
public record UpdateBookingRequest(

        BookingStatus bookingStatus

) {
}
