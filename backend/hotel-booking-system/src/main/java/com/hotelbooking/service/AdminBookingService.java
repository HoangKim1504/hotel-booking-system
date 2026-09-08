package com.hotelbooking.service;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.SimpleBookingResponse;
import com.hotelbooking.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * SERVICE — CRUD Booking.
 *
 * <p>Phân quyền API nằm ở {@code @PreAuthorize} trên Controller — service không
 * hardcode tên role để cho phép/từ chối gọi API.</p>
 */
@Service
@RequiredArgsConstructor
public class AdminBookingService {

    private final BookingService bookingService;

    public PageResponse<SimpleBookingResponse> getBookingsForAdmin(
            int page,
            int size,
            BookingStatus bookingStatus
    ) {
        return bookingService.getBookingList(
                page,
                size,
                bookingStatus,
                null
        );
    }

}
