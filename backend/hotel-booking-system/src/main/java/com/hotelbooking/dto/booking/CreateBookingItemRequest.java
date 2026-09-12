package com.hotelbooking.dto.booking;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateBookingItemRequest(
        @NotBlank(message = "Room type ID is required")
        String roomTypeId,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        @Digits(
                integer = 12,
                fraction = 2,
                message = "Price must have up to 12 integer digits and 2 decimal places"
        )
        BigDecimal price

) {
}
