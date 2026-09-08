package com.hotelbooking.controller;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.SimpleBookingResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.security.AuthUserPrincipal;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public PageResponse<SimpleBookingResponse> getBookingListOfCurrentUser(
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
        AuthUserPrincipal user = securityUtils.currentUser();
        String userId = user.getId();

        return bookingService.getBookingsForUser(page, size, status, userId);
    }
}
