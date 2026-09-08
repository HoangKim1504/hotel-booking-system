package com.hotelbooking.controller;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.SimpleBookingResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.service.AdminBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/bookings")
@RequiredArgsConstructor
@Tag(name = "Admin Bookings")
public class AdminBookingController {

    private final AdminBookingService adminBookingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public PageResponse<SimpleBookingResponse> getBookingListOfUser(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page must be at least 1")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(required = false)
            BookingStatus status
    ) {
        return adminBookingService.getBookingsForAdmin(page, size, status);
    }
}
