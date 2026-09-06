package com.hotelbooking.controller;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomTypeResponse;
import com.hotelbooking.dto.SearchRoomTypeResponse;
import com.hotelbooking.service.RoomTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Types")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping
    @SecurityRequirements // empty = không bắt Authorize khi Try it out login
    public PageResponse<RoomTypeResponse> getRoomTypes(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page must be at least 1")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(required = false)
            @Pattern(
                    regexp = "roomTypeName|price|roomSize|maximumPeople",
                    message = "Sort by must be one of: roomTypeName, price, roomSize, maximumPeople"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return roomTypeService.findAll(page, size, sortBy, order);
    }

    @GetMapping("/{id}")
    @SecurityRequirements // empty = không bắt Authorize khi Try it out login
    public RoomTypeResponse findById(
            @PathVariable String id
    ) {
        return roomTypeService.findById(id);
    }

    @GetMapping("/search")
    public PageResponse<SearchRoomTypeResponse> search(
            @RequestParam("checkInDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkInDate,

            @RequestParam("checkOutDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOutDate,

            @RequestParam("maximumPeople")
            @NotNull(message = "Number of people is required")
            @Min(value = 1, message = "Number of people must be at least 1")
            @Max(value = 10, message = "Number of people cannot be over 10")
            int maximumPeople,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page must be at least 1")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(required = false)
            @Pattern(
                    regexp = "roomTypeName|price|roomSize|maximumPeople",
                    message = "Sort by must be one of: roomTypeName, price, roomSize, maximumPeople"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return roomTypeService.searchByCriteria(checkInDate, checkOutDate, maximumPeople, page, size, sortBy, order);
    }

}
