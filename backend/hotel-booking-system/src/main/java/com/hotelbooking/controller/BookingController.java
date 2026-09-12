package com.hotelbooking.controller;

import com.hotelbooking.dto.*;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.security.AuthUserPrincipal;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.service.PaymentService;
import com.hotelbooking.service.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final PaymentService paymentService;

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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public BookingResponse getBookingDetail(@PathVariable String id) {
        AuthUserPrincipal user = securityUtils.currentUser();
        String userId = user.getId();

        return bookingService.getBookingDetail(id, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request) {
        AuthUserPrincipal user = securityUtils.currentUser();
        String userId = user.getId();
        String username = user.getUsername();

        return bookingService.createNewBooking(request, userId, username);
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(
            @PathVariable String id,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        AuthUserPrincipal user = securityUtils.currentUser();
        String userId = user.getId();
        String username = user.getUsername();

        return paymentService.createPayment(id, request, userId, username, false);
    }

    @PutMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public UpdateBookingResponse cancelBooking(@PathVariable String id) {
        AuthUserPrincipal user = securityUtils.currentUser();
        String userId = user.getId();
        String username = user.getUsername();

        return bookingService.cancelBooking(id, userId, username);
    }

}
