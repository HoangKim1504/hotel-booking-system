package com.hotelbooking.dto;

import com.hotelbooking.enums.RoomTypeStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateRoomTypeRequest(

        @NotBlank(message = "Room type name is required")
        @Size(min = 2, max = 100, message = "Room type name must be between 2 and 100 characters")
        String roomTypeName,

        @Positive(message = "Room size must be greater than 0")
        @DecimalMax(value = "1000.0", message = "Room size must not exceed 1000")
        Double roomSize,

        @NotBlank(message = "At least one facility must be provided")
        String facility,

        @NotNull(message = "Maximum people is required")
        @Min(value = 1, message = "Maximum people must be at least 1")
        @Max(value = 10, message = "Maximum people must not exceed 10")
        int maximumPeople,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        @Digits(integer = 12, fraction = 2, message = "Price must have up to 12 integer digits and 2 decimal places")
        BigDecimal price,

        @NotNull(message = "Room type status is required when updating room type information")
        RoomTypeStatus status

) {
}
