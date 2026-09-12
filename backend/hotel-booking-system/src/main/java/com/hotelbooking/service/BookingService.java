package com.hotelbooking.service;

import com.hotelbooking.dto.*;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.exception.BadRequestException;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.exception.ForbiddenException;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.*;
import com.hotelbooking.utils.PageableUtils;
import com.hotelbooking.validator.DateValidator;
import com.hotelbooking.validator.EntityValidator;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SERVICE — CRUD Booking.
 *
 * <p>Phân quyền API nằm ở {@code @PreAuthorize} trên Controller — service không
 * hardcode tên role để cho phép/từ chối gọi API.</p>
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final RoomRepository roomRepository;
    private final RoomBookingSlotRepository roomBookingSlotRepository;

    private final EntityValidator entityValidator;
    private final DateValidator dateValidator;

    // List các trạng thái Booking đang giữ phòng
    private static final List<BookingStatus> ACTIVE_HOLDING_STATUSES = List.of(
            BookingStatus.PENDING,
            BookingStatus.PAID,
            BookingStatus.CONFIRMED,
            BookingStatus.CHECKED_IN
    );

    // List các trạng thái Booking không thể bị Cancel
    private static final List<BookingStatus> CANNOT_BE_CANCELLED_STATUSES = List.of(
            BookingStatus.CHECKED_IN,
            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED,
            BookingStatus.EXPIRED,
            BookingStatus.REFUNDED
    );

    public PageResponse<SimpleBookingResponse> getBookingsForUser(
            int page,
            int size,
            BookingStatus bookingStatus,
            String userId
    ) {
        return getBookingList(
                page,
                size,
                bookingStatus,
                userId
        );
    }

    /**
     * Lấy danh sách Booking
     */
    public PageResponse<SimpleBookingResponse> getBookingList(int page, int size, BookingStatus bookingStatus, String userId) {
        List<Booking> bookingList;

        // TH userId tồn tại (do từ controller của User)
        if (userId != null) {
            // Kiểm tra tồn tại của userId trong DB
            entityValidator.requireUserByUserId(userId);
            if (bookingStatus == null) {
                bookingList = bookingRepository.findByDeleteFlagFalseAndUserId(userId);
            } else {
                bookingList = bookingRepository.findByDeleteFlagFalseAndUserIdAndStatus(userId, bookingStatus);
            }
        }
        // TH userId không tồn tại (do từ controller của Admin)
        else {
            if (bookingStatus == null) {
                bookingList = bookingRepository.findByDeleteFlagFalse();
            } else {
                bookingList = bookingRepository.findByDeleteFlagFalseAndStatus(bookingStatus);
            }
        }

        if (bookingList.isEmpty()) {
            return new PageResponse<>(List.of(), 0, 0, 0, 0);
        }

        List<SimpleBookingResponse> simpleBookingResponseList = new ArrayList<>();

        for (Booking booking : bookingList) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<BookingItem> bookingItemList =
                    bookingItemRepository.findByDeleteFlagFalseAndBookingId(booking.getId());

            LocalDate checkInDate = booking.getCheckInDate();
            LocalDate checkOutDate = booking.getCheckOutDate();

            for (BookingItem bookingItem : bookingItemList) {
                Integer bookingItemRoomCnt = bookingItem.getQuantity();

                totalAmount = totalAmount.add(calculateTotalBookingMoney(
                        checkInDate,
                        checkOutDate,
                        bookingItem.getPrice(),
                        bookingItemRoomCnt));
            }
            LocalDateTime createdAtDate = LocalDateTime.ofInstant(booking.getCreatedAt(),
                    ZoneId.systemDefault());
            simpleBookingResponseList.add(toSimpleBookingResponse(booking, totalAmount, createdAtDate));
        }
        return PageableUtils.addPagingAttributes(simpleBookingResponseList, page, size);
    }

    /**
     * Lấy thông tin Booking chi tiết của user đang đăng nhập
     */
    public BookingResponse getBookingDetail(String bookingId, String userId) {

        List<BookingItemResponse> bookingItemResponseList = new ArrayList<>();

        // Tìm Booking dựa theo bookingId và userId
        Booking booking = findBookingByIdAndUserId(bookingId, userId);

        BigDecimal totalAmount = BigDecimal.ZERO;

        LocalDate checkInDate = booking.getCheckInDate();
        LocalDate checkOutDate = booking.getCheckOutDate();

        List<BookingItem> bookingItemList = bookingItemRepository.
                findByDeleteFlagFalseAndBookingId(booking.getId());

        for (BookingItem bookingItem : bookingItemList) {
            Integer bookingItemRoomCnt = bookingItem.getQuantity();

            RoomType roomType = entityValidator.requireAdminRoomType(bookingItem.getRoomTypeId());

            totalAmount = totalAmount.add(calculateTotalBookingMoney(
                    checkInDate,
                    checkOutDate,
                    bookingItem.getPrice(),
                    bookingItemRoomCnt));

            bookingItemResponseList.add(toBookingItemResponse(bookingItem,
                    roomType.getRoomTypeName()));
        }

        LocalDateTime createdAtDate = LocalDateTime.ofInstant(booking.getCreatedAt(),
                ZoneId.systemDefault());

        return toBookingResponse(
                booking,
                bookingItemResponseList,
                checkInDate,
                checkOutDate,
                totalAmount,
                createdAtDate);
    }

    /**
     * Insert Booking mới cùng các class liên quan vào DB
     * <p>
     * NOTE: MongoDB không có row-level locking như RDBMS. @Transactional ở đây
     * đảm bảo rollback toàn bộ nếu 1 write giữa chừng thất bại (yêu cầu
     * replica set để multi-document transaction hoạt động).
     */
    @Transactional
    public BookingResponse createNewBooking(CreateBookingRequest request, String userId, String username) {
        // 1. Kiểm tra tồn tại của userId trong DB
        entityValidator.requireUserByUserId(userId);

        // Lấy check-in date và check-out date của booking đó
        LocalDate checkInDate = request.checkInDate();
        LocalDate checkOutDate = request.checkOutDate();

        // 2. Kiểm tra ngày check in và check out
        dateValidator.validateCheckInOutDate(checkInDate, checkOutDate);

        // 3.Lấy Booking đang giữ phòng + overlap với ngày user muốn đặt
        Set<String> validBookingIds = getValidBookingIds(checkInDate, checkOutDate);

        // 4. Lấy Room đã bị các Booking trên chiếm
        Set<String> unavailableRoomIds = getOccupiedRoomIds(validBookingIds);

        // 5. Validate + chuẩn bị booking items
        List<PreparedBookingItem> preparedItems =
                prepareBookingItems(
                        request.items(),
                        checkInDate,
                        checkOutDate,
                        unavailableRoomIds,
                        username
                );

        // 6. Tính total booking
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PreparedBookingItem item : preparedItems) {
            totalAmount = totalAmount.add(item.subtotal());
        }

        // 7. Tạo thời gian booking và expire
        Instant now = Instant.now();
        LocalDateTime createdAt = LocalDateTime.ofInstant(now, ZoneId.systemDefault());

        // PENDING được giữ phòng 15 phút
        LocalDateTime expiresAt = createdAt.plusMinutes(15);

        // 8. Insert booking trước để lấy booking ID
        Booking insertedBooking = insertBooking(
                userId,
                checkInDate,
                checkOutDate,
                expiresAt,
                username,
                now
        );

        // 9. Giữ Room theo từng ngày (tránh double-booking)
        reserveRoomSlots(
                insertedBooking.getId(),
                preparedItems,
                checkInDate,
                checkOutDate
        );

        // 10. Insert booking item + room assignment
        List<BookingItemResponse> bookingItemResponses =
                saveBookingItemsAndAssignments(
                        insertedBooking,
                        preparedItems,
                        username
                );

        // 11. Return response
        return toBookingResponse(
                insertedBooking,
                bookingItemResponses,
                checkInDate,
                checkOutDate,
                totalAmount,
                createdAt
        );
    }

    /*
     * Cancel Booking dựa theo bookingId
     */
    @Transactional
    public UpdateBookingResponse cancelBooking(String bookingId, String userId, String username) {
        // 1. Tìm Booking của user hiện tại
        Booking booking = findBookingByIdAndUserId(bookingId, userId);

        // 2. Validate status có được cancel hay không
        validateBookingCanBeCancelled(booking);

        Instant now = Instant.now();

        // 3. Chuyển Booking sang CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedBy(username);
        booking.setUpdatedAt(now);

        Booking updatedBooking =
                bookingRepository.save(booking);

        // 4. Release RoomBookingSlot
        roomBookingSlotRepository.deleteByBookingId(bookingId);

        // 5. Release RoomAssignment
        releaseRoomAssignments(bookingId, username, now);

        // 6. Return response
        return toUpdateBookingResponse(updatedBooking);
    }

    /**
     * Tính số tiền booking cho 1 loại phòng
     * tổng = [tiền phòng mặc định * (số đêm ngủ * số phòng)]
     */
    private BigDecimal calculateTotalBookingMoney(LocalDate checkInDate, LocalDate checkOutDate,
                                                  BigDecimal price, Integer roomQuantity) {
        // Tính số đêm
        long numberOfNights = calculateNights(checkInDate, checkOutDate);
        // Trả về số tiền booking cho 1 loại phòng
        return price.multiply(BigDecimal.valueOf(numberOfNights * roomQuantity));
    }

    /**
     * Tính số đêm giữa 2 ngày check-in và check-out
     * Đang tính dựa trên ngày check-in và check-out thay vì giờ
     * VD: 13/02 14:00 (IN) ~ 15/02 10:00 (OUT) --> 3 ngày 2 đêm
     */
    private long calculateNights(LocalDate checkInDate, LocalDate checkOutDate) {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    private SimpleBookingResponse toSimpleBookingResponse(Booking booking, BigDecimal totalAmount,
                                                          LocalDateTime createdAtDateTime) {
        return new SimpleBookingResponse(
                booking.getId(),
                booking.getStatus(),
                totalAmount,
                booking.getExpiresAt(),
                createdAtDateTime
        );
    }

    /**
     * Tìm booking dựa trên bookingId và userId
     */
    public Booking findBookingByIdAndUserId(String bookingId, String userId) {
        // Kiểm tra sự tồn tại của userId trong DB
        entityValidator.requireUserByUserId(userId);

        // Tìm Booking dựa theo bookingId
        Booking booking = entityValidator.requireBooking(bookingId);

        // TH userId tại booking và userId truyền vào là 2 userId khác nhau
        if (!userId.equals(booking.getUserId())) {
            throw new ForbiddenException("This booking does not belong to given user: " + userId);
        }

        return booking;
    }

    private BookingItemResponse toBookingItemResponse(BookingItem item, String roomTypeName) {
        return new BookingItemResponse(
                item.getId(),
                item.getRoomTypeId(),
                roomTypeName,
                item.getQuantity(),
                item.getPrice()
        );
    }

    private BookingResponse toBookingResponse(Booking booking, List<BookingItemResponse> items,
                                              LocalDate checkInDate, LocalDate checkOutDate,
                                              BigDecimal totalAmount, LocalDateTime createdAtDateTime) {
        return new BookingResponse(
                booking.getId(),
                booking.getStatus(),
                items,
                checkInDate,
                checkOutDate,
                totalAmount,
                booking.getExpiresAt(),
                createdAtDateTime
        );
    }

    /**
     * Tìm các Booking:
     * <p>
     * 1. deleteFlag = false
     * 2. Status vẫn đang giữ phòng
     * 3. Ngày booking overlap với ngày request
     * 4. Nếu PENDING thì chưa được expired
     */
    private Set<String> getValidBookingIds(
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        LocalDateTime now = LocalDateTime.now();

        return bookingRepository
                .findByDeleteFlagFalseAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        ACTIVE_HOLDING_STATUSES,
                        checkOutDate, // existing.checkInDate < requested.checkOutDate
                        checkInDate // existing.checkOutDate > requested.checkInDate
                )
                .stream()

                /*
                 * Booking PENDING hết hạn
                 * không được tiếp tục giữ Room.
                 */
                .filter(booking ->
                        booking.getStatus() != BookingStatus.PENDING
                                || (booking.getExpiresAt() != null && booking.getExpiresAt().isAfter(now))
                )
                .map(Booking::getId)
                .collect(Collectors.toSet());
    }

    private Set<String> getOccupiedRoomIds(Set<String> bookingIds) {
        // Không có booking overlap
        if (bookingIds.isEmpty()) {
            return new HashSet<>();
        }

        // Tìm BookingItem thuộc các Booking đang giữ phòng
        List<String> bookingItemIds =
                bookingItemRepository.findByBookingIdInAndDeleteFlagFalse(bookingIds)
                        .stream()
                        .map(BookingItem::getId)
                        .toList();

        // Không có BookingItem
        if (bookingItemIds.isEmpty()) {
            return new HashSet<>();
        }

        // Tìm Room đang được assign
        return new HashSet<>(findOccupiedRoomIds(bookingItemIds));
    }

    /**
     * Tìm RoomAssignment đang active của các BookingItem.
     */
    private List<String> findOccupiedRoomIds(List<String> bookingItemIds) {
        return roomAssignmentRepository
                .findByDeleteFlagFalseAndBookingItemIdIn(bookingItemIds)
                .stream()
                .map(RoomAssignment::getRoomId)
                .toList();
    }

    /**
     * Validate từng BookingItem request và chọn Room phù hợp.
     * <p>
     * Chưa insert DB ở bước này.
     */
    private List<PreparedBookingItem> prepareBookingItems(
            List<CreateBookingItemRequest> requestItems,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Set<String> unavailableRoomIds,
            String username
    ) {
        List<PreparedBookingItem> preparedItems = new ArrayList<>();

        // Dùng để không cho roomTypeId bị duplicate trong cùng request
        Set<String> processedRoomTypeIds = new HashSet<>();

        for (CreateBookingItemRequest requestItem : requestItems) {
            String roomTypeId = requestItem.roomTypeId();
            Integer quantity = requestItem.quantity();

            // 1. Check duplicate room type
            if (!processedRoomTypeIds.add(roomTypeId)) {
                throw new BadRequestException("items", "Duplicate room type in booking");
            }

            // 2. Room type phải tồn tại + ACTIVE
            RoomType roomType =
                    entityValidator.requireRoomType(roomTypeId, RoomTypeStatus.ACTIVE);

            // 3. Check giá FE so vói giá hiện trong DB
            validateRoomTypePrice(requestItem.price(), roomType);

            // 4. Tìm Room available
            List<Room> availableRooms =
                    findAvailableRooms(
                            RoomStatus.ACTIVE,
                            roomTypeId,
                            unavailableRoomIds
                    );

            // 5. Check có đủ số lượng room không
            if (availableRooms.size() < quantity) {
                throw new ConflictException(
                        "Room type '" + roomType.getRoomTypeName() + "' does not have enough available rooms"
                );
            }

            // 6. Chọn room theo quantity
            List<String> chosenRoomIds =
                    availableRooms.stream()
                            .limit(quantity)
                            .map(Room::getId)
                            .toList();

            /*
             * 7. Đánh dấu Room vừa chọn là unavailable ngay trong request này.
             *
             * Ví dụ:
             * - Item 1 chọn Room 101
             * - Item 2 không được chọn lại Room 101.
             */
            unavailableRoomIds.addAll(chosenRoomIds);

            /*
             * 8. Tạo Booking Item
             *
             * Lưu ý:
             * - Không lưu requestItem.price().
             * - Giá FE chỉ dùng để check giá cũ.
             * - Giá lưu DB phải lấy từ RoomType trong DB.
             */
            BookingItem bookingItem =
                    createBookingItem(
                            username,
                            roomTypeId,
                            quantity,
                            roomType.getPrice()
                    );

            // 9. Tính sub-total
            BigDecimal subtotal =
                    calculateTotalBookingMoney(
                            checkInDate,
                            checkOutDate,
                            roomType.getPrice(),
                            quantity
                    );

            // 10. Gom thông tin lại
            preparedItems.add(
                    new PreparedBookingItem(
                            bookingItem,
                            roomType,
                            chosenRoomIds,
                            subtotal
                    )
            );
        }

        return preparedItems;
    }

    /**
     * Giá FE gửi lên phải giống giá hiện tại trong DB.
     * <p>
     * Giá FE chỉ dùng để phát hiện user đang nhìn giá cũ.
     */
    private void validateRoomTypePrice(BigDecimal requestPrice, RoomType roomType) {
        BigDecimal currentPrice = roomType.getPrice();

        if (currentPrice.compareTo(requestPrice) != 0) {
            throw new ConflictException(
                    "The price of room type '" +
                            roomType.getRoomTypeName() +
                            "' has changed. Please refresh and try again."
            );
        }
    }

    /**
     * Tìm Room:
     * <p>
     * deleteFlag = false
     * status = ACTIVE
     * roomTypeId = room type user chọn
     * id không nằm trong unavailableRoomIds
     */
    private List<Room> findAvailableRooms(
            RoomStatus status,
            String roomTypeId,
            Set<String> unavailableRoomIds
    ) {
        if (unavailableRoomIds.isEmpty()) {
            return roomRepository
                    .findByDeleteFlagFalseAndStatusAndRoomTypeId(
                            status,
                            roomTypeId
                    );
        }

        return roomRepository
                .findByDeleteFlagFalseAndStatusAndRoomTypeIdAndIdNotIn(
                        status,
                        roomTypeId,
                        unavailableRoomIds
                );
    }

    private BookingItem createBookingItem(String username, String roomTypeId, Integer quantity, BigDecimal price) {
        BookingItem bookingItem = new BookingItem();

        bookingItem.setRoomTypeId(roomTypeId);
        bookingItem.setQuantity(quantity);
        // Snapshot giá tại thời điểm booking
        bookingItem.setPrice(price);
        bookingItem.setDeleteFlag(false);
        bookingItem.setCreatedBy(username);
        bookingItem.setCreatedAt(Instant.now());
        bookingItem.setUpdatedBy(null);
        bookingItem.setUpdatedAt(null);

        return bookingItem;
    }

    private Booking insertBooking(
            String userId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            LocalDateTime expiresAt,
            String username,
            Instant now
    ) {
        Booking newBooking = new Booking();

        newBooking.setUserId(userId);
        // Booking mới đang chờ thanh toán
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setCheckInDate(checkInDate);
        newBooking.setCheckOutDate(checkOutDate);
        newBooking.setExpiresAt(expiresAt);
        newBooking.setDeleteFlag(false);
        newBooking.setCreatedBy(username);
        newBooking.setCreatedAt(now);
        newBooking.setUpdatedBy(null);
        newBooking.setUpdatedAt(null);

        return bookingRepository.save(newBooking);
    }

    private void reserveRoomSlots(
            String bookingId,
            List<PreparedBookingItem> preparedItems,
            LocalDate checkInDate,
            LocalDate checkOutDate
    ) {
        List<RoomBookingSlot> slots = new ArrayList<>();

        for (PreparedBookingItem preparedItem : preparedItems) {
            for (String roomId : preparedItem.roomIds()) {
                LocalDate stayDate = checkInDate;

                while (stayDate.isBefore(checkOutDate)) {
                    slots.add(
                            RoomBookingSlot.builder()
                                    .roomId(roomId)
                                    .bookingId(bookingId)
                                    .stayDate(stayDate)
                                    .createdAt(Instant.now())
                                    .build()
                    );

                    stayDate = stayDate.plusDays(1);
                }
            }
        }

        try {
            roomBookingSlotRepository.saveAll(slots);
        } catch (DuplicateKeyException ex) {
            throw new ConflictException(
                    "One or more rooms have just been booked by another user. " +
                            "Please refresh and try again."
            );
        }
    }

    private List<BookingItemResponse> saveBookingItemsAndAssignments(
            Booking booking,
            List<PreparedBookingItem> preparedItems,
            String username
    ) {
        List<BookingItemResponse> responses = new ArrayList<>();

        for (PreparedBookingItem preparedItem : preparedItems) {
            BookingItem bookingItem = preparedItem.bookingItem();

            // Gắn Booking ID
            bookingItem.setBookingId(booking.getId());

            // Insert BookingItem
            BookingItem insertedBookingItem = bookingItemRepository.save(bookingItem);

            // Insert RoomAssignments
            for (String roomId : preparedItem.roomIds()) {
                // Assignment thuộc BookingItem vừa insert
                String bookingItemId = insertedBookingItem.getId();

                RoomAssignment roomAssignment = createRoomAssignment(bookingItemId, roomId, username);

                roomAssignmentRepository.save(roomAssignment);
            }

            // Create response
            BookingItemResponse response =
                    toBookingItemResponse(
                            insertedBookingItem,
                            preparedItem.roomType().getRoomTypeName()
                    );

            responses.add(response);
        }

        return responses;
    }

    private RoomAssignment createRoomAssignment(String bookingItemId, String roomId, String username) {
        RoomAssignment assignment = new RoomAssignment();

        assignment.setBookingItemId(bookingItemId);
        assignment.setRoomId(roomId);
        assignment.setDeleteFlag(false);
        assignment.setCreatedBy(username);
        assignment.setCreatedAt(Instant.now());
        assignment.setUpdatedBy(null);
        assignment.setUpdatedAt(null);

        return assignment;
    }

    private void validateBookingCanBeCancelled(Booking booking) {
        BookingStatus currentStatus = booking.getStatus();

        if (CANNOT_BE_CANCELLED_STATUSES.contains(currentStatus)) {
            throw new ConflictException("Booking with status " + currentStatus + " cannot be cancelled");
        }
    }

    public void releaseRoomAssignments(String bookingId, String username, Instant now) {
        List<String> bookingItemIds =
                bookingItemRepository
                        .findByDeleteFlagFalseAndBookingId(bookingId)
                        .stream()
                        .map(BookingItem::getId)
                        .toList();

        if (bookingItemIds.isEmpty()) {
            return;
        }

        List<RoomAssignment> assignments =
                roomAssignmentRepository.findByDeleteFlagFalseAndBookingItemIdIn(bookingItemIds);

        for (RoomAssignment assignment : assignments) {
            assignment.setDeleteFlag(true);
            assignment.setUpdatedBy(username);
            assignment.setUpdatedAt(now);
        }

        roomAssignmentRepository.saveAll(assignments);
    }

    public UpdateBookingResponse toUpdateBookingResponse(Booking booking) {
        return new UpdateBookingResponse(
                booking.getId(),
                booking.getStatus()
        );
    }

}
