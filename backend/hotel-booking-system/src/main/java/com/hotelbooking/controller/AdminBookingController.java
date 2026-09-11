package com.hotelbooking.controller;

import com.hotelbooking.dto.*;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.service.AdminBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public BookingResponse getBookingDetail(
            @PathVariable String id,

            @NotBlank(message = "User ID must not be empty")
            @RequestParam
            String userId
    ) {
        return adminBookingService.getBookingDetailForAdmin(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public BookingResponse createBooking(
            @Valid @RequestBody
            CreateBookingRequest request,

            @RequestParam
            String userId,

            Authentication authentication
    ) {
        String username = authentication.getName();
        return adminBookingService.createNewBookingForAdmin(request, userId, username);
    }

    @PutMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public UpdateBookingResponse cancelBooking(
            @PathVariable
            String id,

            @NotBlank(message = "User ID is required")
            @RequestParam
            String userId,

            Authentication authentication
    ) {
        String username = authentication.getName();

        return adminBookingService.cancelBooking(id, userId, username);
    }

}
