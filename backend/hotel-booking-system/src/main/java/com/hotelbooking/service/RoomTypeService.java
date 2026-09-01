package com.hotelbooking.service;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.RoomTypeResponse;
import com.hotelbooking.dto.SearchRoomTypeResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.model.BookingItem;
import com.hotelbooking.model.Room;
import com.hotelbooking.model.RoomAssignment;
import com.hotelbooking.model.RoomType;
import com.hotelbooking.repository.*;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final EntityValidator entityValidator;

    /**
     * Search toàn bộ Room Type, có phân trang và max record mỗi trang
     */
    public PageResponse<RoomTypeResponse> findAll(int currentPage, int pageSize, String sortBy, String order) {
        Pageable pageable =
                createPageable(currentPage, pageSize, sortBy, order);

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
        return toRoomTypeResponse(entityValidator.requireRomeType(id));
    }

    /**
     * Search Room Type, có phân trang và max record mỗi trang, dựa trên điều kiện cho trước
     */
    public PageResponse<SearchRoomTypeResponse> searchByCriteria(LocalDate checkInDate, LocalDate checkOutDate,
                                                                 int maxPeople, int currentPage, int pageSize,
                                                                 String sortBy, String order) {
        List<String> eligibleRoomTypeIds;

        // 1. Validate input
        validateCheckInOutDate(checkInDate, checkOutDate);

        // 2. Lấy RoomType phù hợp: deleteFlag = false, status = ACTIVE, maximumPeople >= people
        List<RoomType> eligibleRoomTypes = findEligibleRoomTypes(maxPeople);

        if (eligibleRoomTypes.isEmpty()) {
            return new PageResponse<>(
                    List.of(),
                    currentPage,
                    pageSize,
                    0,
                    0
            );
        } else {
            eligibleRoomTypeIds = eligibleRoomTypes
                    .stream()
                    .map(RoomType::getId)
                    .toList();
        }

        // 3. Lấy BookingItem đang overlap và booking còn hiệu lực
        List<BookingItem> occupiedBookingItems = findOccupiedBookingItems(checkInDate, checkOutDate);

        // 4. Lấy bookingItemId
        List<String> occupiedBookingItemsIds =
                occupiedBookingItems
                        .stream()
                        .map(BookingItem::getId)
                        .toList();

        // 5. Tìm Room đang bị chiếm
        Set<String> occupiedRoomIds = findOccupiedRoomIds(occupiedBookingItemsIds);

        // 6. Lấy Room còn khả dụng: deleteFlag = false, status = ACTIVE, roomTypeId thuộc danh sách eligible,
        // id không nằm trong occupiedRoomIds
        List<Room> availableRooms = findAvailableRooms(RoomStatus.ACTIVE, eligibleRoomTypeIds, occupiedRoomIds);

        // 7. Group theo roomTypeId → tính availableQuantity
        Map<String, Long> availableCountByRoomTypeId =
                availableRooms
                        .stream()
                        .collect(Collectors.groupingBy(Room::getRoomTypeId, Collectors.counting()));

        // 8. Build response
        List<SearchRoomTypeResponse> results = buildSearchResults(eligibleRoomTypes, availableCountByRoomTypeId);

        // 9. Sort
        List<SearchRoomTypeResponse> sortedResponses = sortSearchResults(results, sortBy, order);

        // 10. Pagination
        return paginateSearchResults(results, currentPage, pageSize);
    }

    private Pageable createPageable(int currentPage, int pageSize, String sortBy, String order) {
        Pageable pageable;

        // Set vị trí trang hiện tại và lượng record max mỗi trang
        // Lưu ý: API bắt đầu từ page = 1, Spring Data Pageable bắt đầu từ page = 0
        int pageNumber = currentPage - 1;

        // Không truyền sortBy → chỉ pagination
        if (sortBy == null || sortBy.isBlank()) {
            pageable = PageRequest.of(pageNumber, pageSize);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.Direction.fromString(order), sortBy);
        }
        return pageable;
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

    /**
     * Check ngày check-in phải trước ngày check-out.
     */
    private void validateCheckInOutDate(LocalDate checkIn, LocalDate checkOut) {
        if (!checkIn.isBefore(checkOut)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }
    }

    /**
     * Lấy RoomType hợp lệ:
     * - deleteFlag = false
     * - status = ACTIVE
     * - maximumPeople >= maxPeople
     */
    private List<RoomType> findEligibleRoomTypes(int maxPeople) {
        List<RoomType> roomTypes = roomTypeRepository.findByDeleteFlagFalseAndStatus(RoomTypeStatus.ACTIVE);
        return roomTypes
                .stream()
                .filter(roomType -> roomType.getMaximumPeople() >= maxPeople)
                .toList();
    }

    private List<BookingItem> findOccupiedBookingItems(LocalDate checkInDate, LocalDate checkOutDate) {
        // Tìm BookingItem bị overlap theo checkInDate và checkOutDate
        List<BookingItem> overlappingBookingItems =
                bookingItemRepository.findByDeleteFlagFalseAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        checkOutDate, checkInDate);

        // Tìm loại booking không còn hiệu lực: CANCELLED / EXPIRED / REFUNDED...
        List<BookingStatus> activeStatuses = List.of(
                BookingStatus.PENDING,
                BookingStatus.PAID,
                BookingStatus.CONFIRMED,
                BookingStatus.CHECKED_IN
        );
        List<String> validBookingIds = getValidBookingIds(activeStatuses);

        return overlappingBookingItems
                .stream()
                .filter(item -> validBookingIds.contains(item.getBookingId()))
                .toList();
    }

    private List<String> getValidBookingIds(List<BookingStatus> activeStatuses) {
        return bookingRepository.findByDeleteFlagFalseAndStatusIn(activeStatuses);
    }

    private Set<String> findOccupiedRoomIds(List<String> occupiedBookingItemIds) {
        if (occupiedBookingItemIds.isEmpty()) {
            return Collections.emptySet();
        }

        return roomAssignmentRepository
                .findByDeleteFlagFalseAndBookingItemIdIn(occupiedBookingItemIds)
                .stream()
                .map(RoomAssignment::getRoomId)
                .collect(Collectors.toSet());
    }

    private List<Room> findAvailableRooms(RoomStatus roomStatus, List<String> eligibleRoomTypeIds,
                                          Set<String> occupiedRoomIds) {
        return roomRepository.findByDeleteFlagFalseAndStatusAndRoomTypeIdInAndIdNotIn(
                roomStatus,
                eligibleRoomTypeIds,
                occupiedRoomIds
        );
    }

    private List<SearchRoomTypeResponse> buildSearchResults(List<RoomType> eligibleRoomTypes,
                                                            Map<String, Long> availableCountByRoomTypeId) {

        // eligibleRoomTypes
        // → lọc những RoomType có phòng available
        // → map từng RoomType thành SearchRoomTypeResponse
        // → lấy availableQuantity từ Map
        // → trả về List<SearchRoomTypeResponse>
        return eligibleRoomTypes
                .stream()
                .filter(roomType -> availableCountByRoomTypeId.containsKey(roomType.getId()))
                .map(roomType -> SearchRoomTypeResponse.builder()
                        .id(roomType.getId())
                        .roomTypeName(roomType.getRoomTypeName())
                        .roomSize(roomType.getRoomSize())
                        .facility(roomType.getFacility())
                        .maximumPeople(roomType.getMaximumPeople())
                        .price(roomType.getPrice())
                        .availableQuantity(Math.toIntExact(availableCountByRoomTypeId.get(roomType.getId())))
                        .build())
                .toList();
    }

    private List<SearchRoomTypeResponse> sortSearchResults(List<SearchRoomTypeResponse> responses,
                                                           String sortBy, String order) {

        if (sortBy == null || sortBy.isBlank()) {
            return responses;
        }

        Comparator<SearchRoomTypeResponse> comparator;

        switch (sortBy) {
            case "price" -> comparator = Comparator.comparing(SearchRoomTypeResponse::price);
            case "roomTypeName" -> comparator = Comparator.comparing(SearchRoomTypeResponse::roomTypeName);
            case "roomSize" -> comparator = Comparator.comparing(SearchRoomTypeResponse::roomSize);
            case "maximumPeople" -> comparator = Comparator.comparing(SearchRoomTypeResponse::maximumPeople);
            default -> {
                return responses;
            }
        }

        if ("DESC".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        return responses.stream()
                .sorted(comparator)
                .toList();
    }

    private PageResponse<SearchRoomTypeResponse> paginateSearchResults(List<SearchRoomTypeResponse> responses,
                                                                       int currentPage, int pageSize) {

        int totalRecords = responses.size();
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
        int fromIndex = (currentPage - 1) * pageSize;
        if (fromIndex >= totalRecords) {
            return new PageResponse<>(
                    List.of(),
                    currentPage,
                    pageSize,
                    totalRecords,
                    totalPages
            );
        }
        int toIndex = Math.min(fromIndex + pageSize, totalRecords);
        List<SearchRoomTypeResponse> items = responses.subList(fromIndex, toIndex);

        return new PageResponse<>(
                items,
                currentPage,
                pageSize,
                totalRecords,
                totalPages
        );
    }

}
