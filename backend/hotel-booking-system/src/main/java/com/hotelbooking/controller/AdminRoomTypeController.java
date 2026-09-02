package com.hotelbooking.controller;

import com.hotelbooking.dto.CreateRoomTypeRequest;
import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomTypeResponse;
import com.hotelbooking.dto.UpdateRoomTypeRequest;
import com.hotelbooking.service.AdminRoomTypeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/room-types")
@RequiredArgsConstructor
@Tag(name = "Room Types")
public class AdminRoomTypeController {

    private final AdminRoomTypeService adminRoomTypeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
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
                    regexp = "roomTypeName|price|roomSize|maximumPeople|status",
                    message = "Sort by must be one of: roomTypeName, price, roomSize, maximumPeople, status"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return adminRoomTypeService.findAll(page, size, sortBy, order);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public RoomTypeResponse findById(
            @PathVariable String id
    ) {
        return adminRoomTypeService.findById(id);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public PageResponse<RoomTypeResponse> search(
            @RequestParam(required = false)
            String roomTypeName,

            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "Page must be at least 1")
            int page,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size,

            @RequestParam(defaultValue = "roomTypeName")
            @Pattern(
                    regexp = "roomTypeName|price|roomSize|maximumPeople|status",
                    message = "Sort by must be one of: roomTypeName, price, roomSize, maximumPeople, status"
            )
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            @Pattern(
                    regexp = "(?i)ASC|DESC",
                    message = "Order must be ASC or DESC"
            )
            String order
    ) {
        return adminRoomTypeService.searchByCriteria(roomTypeName, page, size, sortBy, order);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public RoomTypeResponse create(
            @Valid @RequestBody CreateRoomTypeRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return adminRoomTypeService.create(request, username);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('USER_UPDATE')")
    public RoomTypeResponse update(
            @Valid @RequestBody UpdateRoomTypeRequest request,
            @PathVariable String id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        return adminRoomTypeService.update(request, id, username);
    }

}
