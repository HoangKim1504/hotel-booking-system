package com.hotelbooking.service;

import com.hotelbooking.dto.CreateRoomRequest;
import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomResponse;
import com.hotelbooking.dto.UpdateRoomRequest;
import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.exception.BadRequestException;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.RoomAssignmentRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.repository.RoomTypeRepository;
import com.hotelbooking.utils.PageableUtils;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;

    private final EntityValidator entityValidator;
    private final MongoTemplate mongoTemplate;

    Instant now = Instant.now();

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
     * Tìm room tương ứng vs MongoID. Room phải chưa được soft-delete
     */
    public RoomResponse findById(String id) {
        return toRoomResponse(entityValidator.requireAdminRoom(id));
    }

    /**
     * Search Room Type, có phân trang và max record mỗi trang, dựa trên điều kiện cho trước
     */
    public PageResponse<RoomResponse> searchByCriteria(String roomTypeName, RoomStatus roomStatus, Integer roomNumber,
                                                       int currentPage, int pageSize, String sortBy, String order) {

        // 1. Create pageable
        Pageable pageable = PageableUtils.createPageable(currentPage, pageSize, sortBy, order);

        // 2. Danh sách điều kiện search
        List<Criteria> criteriaList = new ArrayList<>();

        // Chỉ lấy Room chưa bị soft-delete
        criteriaList.add(
                Criteria.where("deleteFlag").is(false)
        );

        // 3. Search theo roomNumber
        if (roomNumber != null) {
            criteriaList.add(
                    Criteria.where("roomNumber").is(roomNumber)
            );
        }

        // 4. Search theo status
        if (roomStatus != null) {
            criteriaList.add(
                    Criteria.where("status").is(roomStatus)
            );
        }

        // 5. Search theo RoomType name
        if (roomTypeName != null && !roomTypeName.isBlank()) {

            List<String> roomTypeIds = roomTypeRepository
                    .findByRoomTypeNameContainingIgnoreCaseAndDeleteFlagFalse(
                            roomTypeName.trim()
                    )
                    .stream()
                    .map(RoomType::getId)
                    .toList();

            // Không có RoomType matching
            if (roomTypeIds.isEmpty()) {
                return new PageResponse<>(
                        List.of(),
                        currentPage,
                        pageSize,
                        0,
                        0
                );
            }

            criteriaList.add(
                    Criteria.where("roomTypeId").in(roomTypeIds)
            );
        }

        // 6. Combine criteria
        Criteria criteria = new Criteria().andOperator(
                criteriaList.toArray(new Criteria[0])
        );

        // 7. Query + pagination + sort
        Query query = new Query(criteria).with(pageable);

        List<Room> rooms = mongoTemplate.find(
                query,
                Room.class
        );

        // 8. Count
        long totalElements = mongoTemplate.count(
                new Query(criteria),
                Room.class
        );

        Page<Room> roomPage = new PageImpl<>(
                rooms,
                pageable,
                totalElements
        );

        // 9. Entity -> DTO
        List<RoomResponse> roomList = roomPage.getContent()
                .stream()
                .map(this::toRoomResponse)
                .toList();

        // 10. Return pagination
        return addPagingAttributes(
                roomList,
                currentPage,
                pageSize,
                roomPage
        );
    }

    /**
     * Insert Room mới vào DB
     */
    public RoomResponse create(CreateRoomRequest request, String username) {
        // Check ký tự đầu của roomNumber giống với roomFloor không
        validateRoomNumberWithFloor(request.roomNumber(), request.floorNumber());

        // Tìm RoomType theo tên. Không tìm thấy -> 404
        RoomType existingRoomType = entityValidator.requireAdminRoomTypeByName(request.roomTypeName());

        // TH tồn tại Room Number chung số thì sẽ trả mã lỗi 409
        if (roomRepository.existsByRoomNumberAndDeleteFlagFalse(request.roomNumber())) {
            throw new ConflictException("Current room number already exists");
        }

        Room newRoom = setNewRoom(request, existingRoomType.getId(), username);

        return toRoomResponse(roomRepository.save(newRoom));
    }

    public RoomResponse update(UpdateRoomRequest request, String id, String username) {
        // Check ký tự đầu của roomNumber giống với roomFloor không
        validateRoomNumberWithFloor(request.roomNumber(), request.floorNumber());

        // Tìm room tồn tại
        Room existingRoom = entityValidator.requireAdminRoom(id);

        // Tìm room type tồn tại
        RoomType existingRoomType = entityValidator.requireAdminRoomType(existingRoom.getRoomTypeId());

        // Kiểm tra Room khác có cùng roomNumber
        if (roomRepository.existsByRoomNumberAndIdNotAndDeleteFlagFalse(request.roomNumber(), id)) {
            throw new ConflictException("Current room number already exists in another room");
        }

        // Kiểm tra phòng có đang được dùng không
        checkOccupiedRoomId(id, true);

        Room updateRoom = setUpdateRoom(request, existingRoom, id, username);

        return buildRoomResponse(roomRepository.save(updateRoom), existingRoomType.getRoomTypeName());
    }

    /**
     * Xoá Room dựa trên MongodID của Room
     */
    public void delete(String id, String username) {
        // Tìm room tồn tại
        Room existingRoom = entityValidator.requireAdminRoom(id);

        // Kiểm tra phòng có đang được dùng không
        checkOccupiedRoomId(id, false);

        Room softDeleteRoom = setSoftDeleteRoom(existingRoom, username);

        // Thực thi soft-delete và update DB
        roomRepository.save(softDeleteRoom);
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

    /**
     * Check ký tự đầu của roomNumber giống với roomFloor không
     */
    private void validateRoomNumberWithFloor(Integer roomNumber, Integer floorNumber) {
        int roomFloor = roomNumber / 100;

        if (roomFloor != floorNumber) {
            throw new BadRequestException("roomNumber", "Room number must match the floor number");
        }
    }

    private Room setNewRoom(CreateRoomRequest request, String roomTypeId, String username) {
        Room room = new Room();

        room.setRoomTypeId(roomTypeId);
        room.setRoomNumber(request.roomNumber());
        room.setFloorNumber(request.floorNumber());
        room.setStatus(RoomStatus.ACTIVE);
        room.setDeleteFlag(false);
        room.setCreatedBy(username);
        room.setCreatedAt(now);
        room.setUpdatedBy(null);
        room.setUpdatedAt(null);

        return room;
    }

    private Room setUpdateRoom(UpdateRoomRequest request, Room existingRoom, String id, String username) {
        existingRoom.setRoomTypeId(id);
        existingRoom.setRoomNumber(request.roomNumber());
        existingRoom.setFloorNumber(request.floorNumber());
        existingRoom.setStatus(request.status());
        existingRoom.setUpdatedBy(username);
        existingRoom.setUpdatedAt(now);

        return existingRoom;
    }

    private void checkOccupiedRoomId(String id, boolean updateFlag) {
        // Kiểm tra phòng có đang được dùng không
        boolean occupiedRoomId = roomAssignmentRepository.existsByRoomIdAndDeleteFlagFalse(id);

        if (occupiedRoomId && updateFlag) {
            throw new ConflictException("Room is currently assigned and cannot be updated");
        } else if (occupiedRoomId) {
            throw new ConflictException("Room is currently assigned and cannot be deleted");
        }
    }

    private Room setSoftDeleteRoom(Room room, String username) {
        room.setStatus(RoomStatus.OUT_OF_SERVICE);
        room.setDeleteFlag(true);
        room.setUpdatedBy(username);
        room.setUpdatedAt(now);
        return room;
    }

}
