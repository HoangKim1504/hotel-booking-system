package com.hotelbooking.controller;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomTypeResponse;
import com.hotelbooking.service.RoomTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room-types" )
@RequiredArgsConstructor
@Tag(name = "Room Types" )
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    @SecurityRequirements // empty = không bắt Authorize khi Try it out login
    public PageResponse<RoomTypeResponse> getRoomTypes(
            @RequestParam(defaultValue = "1" )
            @Min(value = 1, message = "Page must be at least 1" )
            int page,

            @RequestParam(defaultValue = "10" )
            @Min(value = 1, message = "Size must be at least 1" )
            @Max(value = 100, message = "Size must not exceed 100" )
            int size,

            @RequestParam(required = false)
            @Pattern(
                    regexp = "roomTypeName|price|roomSize|maximumPeople",
                    message = "Sort by must be one of: roomTypeName, price, roomSize, maximumPeople"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC" )
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return roomTypeService.findAll(page, size, sortBy, order);
    }

}
