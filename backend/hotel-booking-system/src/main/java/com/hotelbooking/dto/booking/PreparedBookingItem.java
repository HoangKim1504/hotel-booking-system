package com.hotelbooking.dto.booking;

import com.hotelbooking.model.BookingItem;
import com.hotelbooking.model.RoomType;

import java.math.BigDecimal;
import java.util.List;

public record PreparedBookingItem(
        BookingItem bookingItem,
        RoomType roomType,
        List<String> roomIds,
        BigDecimal subtotal
) {
}
