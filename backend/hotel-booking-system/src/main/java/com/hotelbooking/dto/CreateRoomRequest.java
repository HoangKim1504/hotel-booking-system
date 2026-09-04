package com.hotelbooking.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRoomRequest(

        @NotBlank(message = "Room type name is required")
        String roomTypeName,

        @NotNull(message = "Room number is required")
        @Min(value = 100, message = "Room number must be at least 100")
        @Max(value = 999, message = "Room number must not exceed 999")
        int roomNumber,

        @NotNull(message = "Floor number is required")
        @Min(value = 1, message = "Floor number must be at least 1")
        @Max(value = 9, message = "Floor number must not exceed 9")
        int floorNumber

) {
}