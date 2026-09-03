package com.hotelbooking.service;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomResponse;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.utils.PageableUtils;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomService {

    private final RoomRepository roomRepository;
    private final EntityValidator entityValidator;

    /**
     * Search toàn bộ Room, có phân trang và max record mỗi trang
     */
    public PageResponse<RoomResponse> findAll(int currentPage, int pageSize, String sortBy, String order) {

        // Create pageable
        Pageable pageable = PageableUtils.createPageable(currentPage, pageSize, sortBy, order);

        // Query DB
        Page<Room> roomPage = roomRepository.findAllByDeleteFlagFalse(pageable);

        // Map Entity → DTO
        List<RoomResponse> roomList = roomPage.getContent()
                .stream()
                .map(this::toRoomResponse)
                .toList();

        return addPagingAttributes(roomList, currentPage, pageSize, roomPage);
    }

    /**
     * Convert Room sang RoomResponse.
     * Tìm RoomType tương ứng để lấy roomTypeName trước khi tạo response.
     */
    private RoomResponse toRoomResponse(Room room) {
        RoomType roomType = entityValidator.requireAdminRoomType(room.getRoomTypeId());

        return buildRoomResponse(
                room,
                roomType.getRoomTypeName()
        );
    }

    /**
     * Tạo RoomResponse từ Room và roomTypeName đã lấy được.
     */
    private RoomResponse buildRoomResponse(
            Room room,
            String roomTypeName
    ) {
        return new RoomResponse(
                room.getId(),
                room.getRoomTypeId(),
                roomTypeName,
                room.getRoomNumber(),
                room.getFloorNumber(),
                room.getStatus()
        );
    }

    /**
     * Tính toán và đẩy các thuộc tính phân trang/sắp xếp sang view
     */
    private PageResponse<RoomResponse> addPagingAttributes(
            List<RoomResponse> roomResponsesList,
            int currentPage,
            int pageSize,
            Page<Room> roomPage
    ) {
        long totalRecords = roomPage.getTotalElements();
        int totalPages = roomPage.getTotalPages();

        return new PageResponse<>(
                roomResponsesList,
                currentPage,
                pageSize,
                totalRecords,
                totalPages
        );
    }

}
