package com.hotelbooking.service;

import com.hotelbooking.dto.CreateRoomTypeRequest;
import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomTypeResponse;
import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.RoomTypeRepository;
import com.hotelbooking.utils.PageableUtils;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final EntityValidator entityValidator;

    /**
     * Search toàn bộ Room Type, có phân trang và max record mỗi trang
     */
    public PageResponse<RoomTypeResponse> findAll(int currentPage, int pageSize, String sortBy, String order) {

        // Create pageable
        Pageable pageable = PageableUtils.createPageable(currentPage, pageSize, sortBy, order);

        // Query DB
        Page<RoomType> roomTypePage = roomTypeRepository.findAllByDeleteFlagFalse(pageable);

        // Map Entity → DTO
        List<RoomTypeResponse> roomTypeList = roomTypePage.getContent()
                .stream()
                .map(this::toRoomTypeResponse)
                .toList();

        return addPagingAttributes(roomTypeList, currentPage, pageSize, roomTypePage);
    }

    /**
     * Tìm room type tương ứng vs MongoID. Room Type phải chưa được soft-delete
     */
    public RoomTypeResponse findById(String id) {
        return toRoomTypeResponse(entityValidator.requireAdminRoomType(id));
    }

    /**
     * Search Room Type, có phân trang và max record mỗi trang, dựa trên điều kiện cho trước
     */
    public PageResponse<RoomTypeResponse> searchByCriteria(String roomTypeName, int currentPage, int pageSize,
                                                           String sortBy, String order) {

        // 1. Create pageable
        Pageable pageable = PageableUtils.createPageable(currentPage, pageSize, sortBy, order);

        // 2. Search RoomType
        Page<RoomType> roomTypePage;

        if (roomTypeName == null || roomTypeName.isBlank()) {
            roomTypePage = roomTypeRepository.findAllByDeleteFlagFalse(pageable);
        } else {
            roomTypePage =
                    roomTypeRepository.findByRoomTypeNameContainingIgnoreCaseAndDeleteFlagFalse(
                            roomTypeName.trim(),
                            pageable
                    );
        }

        // 3. Convert RoomType -> Response
        List<RoomTypeResponse> responses =
                roomTypePage.getContent()
                        .stream()
                        .map(this::toRoomTypeResponse)
                        .toList();

        // 4. Return PageResponse
        return addPagingAttributes(responses, currentPage, pageSize, roomTypePage);
    }

    /**
     * Insert Room Type mới vào DB
     */
    public RoomTypeResponse create(CreateRoomTypeRequest request, String username) {
        // TH tồn tại Room Type chung tên thì sẽ trả mã lỗi 409
        if (roomTypeRepository.existsByRoomTypeNameAndDeleteFlagFalse(request.roomTypeName().trim())) {
            throw new ConflictException("Room type name already exists");
        }

        RoomType newRoomType = setNewRoomType(request, username);

        return toRoomTypeResponse(roomTypeRepository.save(newRoomType));
    }

    /**
     * Convert sang class Response để trả về Controller
     */
    private RoomTypeResponse toRoomTypeResponse(RoomType roomType) {
        return new RoomTypeResponse(
                roomType.getId(),
                roomType.getRoomTypeName(),
                roomType.getRoomSize(),
                roomType.getFacility(),
                roomType.getMaximumPeople(),
                roomType.getPrice()
        );
    }

    /**
     * Tính toán và đẩy các thuộc tính phân trang/sắp xếp sang view
     */
    private PageResponse<RoomTypeResponse> addPagingAttributes(
            List<RoomTypeResponse> roomTypeResponsesList,
            int currentPage,
            int pageSize,
            Page<RoomType> roomTypePage
    ) {
        long totalRecords = roomTypePage.getTotalElements();
        int totalPages = roomTypePage.getTotalPages();

        return new PageResponse<>(
                roomTypeResponsesList,
                currentPage,
                pageSize,
                totalRecords,
                totalPages
        );
    }

    private RoomType setNewRoomType(CreateRoomTypeRequest request, String username) {
        RoomType roomType = new RoomType();
        Instant now = Instant.now();

        roomType.setRoomTypeName(request.roomTypeName());
        roomType.setRoomSize(request.roomSize());
        roomType.setFacility(request.facility());
        roomType.setMaximumPeople(request.maximumPeople());
        roomType.setPrice(request.price());
        roomType.setStatus(RoomTypeStatus.ACTIVE);
        roomType.setDeleteFlag(false);
        roomType.setCreatedBy(username);
        roomType.setCreatedAt(now);
        roomType.setUpdatedBy(null);
        roomType.setUpdatedAt(null);

        return roomType;
    }

}
