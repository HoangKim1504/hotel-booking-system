package com.hotelbooking.controller;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomResponse;
import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.service.AdminRoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rooms")
@RequiredArgsConstructor
@Tag(name = "Rooms")
public class AdminRoomController {

    private final AdminRoomService adminRoomService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public PageResponse<RoomResponse> getRooms(
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page must be at least 1")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(required = false)
            @Pattern(
                    regexp = "roomTypeName|roomNumber|floorNumber",
                    message = "Sort by must be one of: roomTypeName, roomNumber, floorNumber"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return adminRoomService.findAll(page, size, sortBy, order);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public PageResponse<RoomResponse> search(
            @RequestParam(required = false)
            String roomTypeName,

            @RequestParam(required = false)
            @Pattern(
                    regexp = "(?i)ACTIVE|MAINTENANCE|OUT_OF_SERVICE",
                    message = "Room status must be one of: ACTIVE, MAINTENANCE, OUT_OF_SERVICE"
            )
            RoomStatus roomStatus,

            @RequestParam(required = false)
            @Min(value = 100, message = "Room number must be at least 100")
            @Max(value = 999, message = "Room number must not exceed 999")
            Integer roomNumber,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page must be at least 1")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(required = false)
            @Pattern(
                    regexp = "roomNumber|floorNumber|status",
                    message = "Sort by must be one of: roomNumber, floorNumber, status"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return adminRoomService.searchByCriteria(roomTypeName, roomStatus, roomNumber, page, size, sortBy, order);
    }

}
