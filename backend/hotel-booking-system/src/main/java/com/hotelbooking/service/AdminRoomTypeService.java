package com.hotelbooking.service;

import com.hotelbooking.dto.CreateRoomTypeRequest;
import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomTypeResponse;
import com.hotelbooking.dto.UpdateRoomTypeRequest;
import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.RoomRepository;
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
    private final RoomRepository roomRepository;
    private final EntityValidator entityValidator;

    Instant now = Instant.now();

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
     * Search Room Type có sẵn và update data với Room Type input vào DB
     */
    public RoomTypeResponse update(UpdateRoomTypeRequest request, String id, String username) {
        // Tìm room type tương ứng vs MongoID. Không tìm thấy -> 404
        RoomType existingRoomType = entityValidator.requireAdminRoomType(id);

        // TH tồn tại Room Type chung tên thì sẽ trả mã lỗi 409
        if (request.roomTypeName().trim().equals(existingRoomType.getRoomTypeName().trim())) {
            throw new ConflictException("Room type name already exists");
        }

        RoomType updateRoomType = setCurrentRoomType(request, existingRoomType, username);

        return toRoomTypeResponse(roomTypeRepository.save(updateRoomType));
    }

    /**
     * Xoá Room Type dựa trên MongodID của Room Type
     */
    public void delete(String id, String username) {
        // Tìm room type tương ứng vs MongoID. Không tìm thấy -> 404
        RoomType existingRoomType = entityValidator.requireAdminRoomType(id);

        // Tìm list các phòng dựa trên
        List<Room> roomsAvailableList = roomRepository.findByRoomTypeIdAndDeleteFlagFalse(id);
        if (!roomsAvailableList.isEmpty()) {
            throw new ConflictException("Cannot delete room type because it still has associated rooms");
        }

        RoomType softDeleteRoomType = setSoftDeleteRoomType(existingRoomType, username);

        // Thực thi soft-delete và update DB
        roomTypeRepository.save(softDeleteRoomType);
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

    private RoomType setCurrentRoomType(UpdateRoomTypeRequest request, RoomType existingRoomType, String username) {
        existingRoomType.setRoomTypeName(request.roomTypeName());
        existingRoomType.setRoomSize(request.roomSize());
        existingRoomType.setFacility(request.facility());
        existingRoomType.setMaximumPeople(request.maximumPeople());
        existingRoomType.setPrice(request.price());
        existingRoomType.setStatus(request.status());
        existingRoomType.setUpdatedBy(username);
        existingRoomType.setUpdatedAt(now);

        return existingRoomType;
    }

    private RoomType setSoftDeleteRoomType(RoomType existingRoomType, String username) {
        existingRoomType.setStatus(RoomTypeStatus.INACTIVE);
        existingRoomType.setDeleteFlag(true);
        existingRoomType.setUpdatedBy(username);
        existingRoomType.setUpdatedAt(now);

        return existingRoomType;
    }

}
