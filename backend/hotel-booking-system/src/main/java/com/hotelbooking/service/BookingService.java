package com.hotelbooking.service;

import com.hotelbooking.dto.*;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.enums.RoomTypeStatus;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.exception.ForbiddenException;
import com.hotelbooking.model.*;
import com.hotelbooking.repository.BookingItemRepository;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomAssignmentRepository;
import com.hotelbooking.repository.RoomRepository;
import com.hotelbooking.utils.PageableUtils;
import com.hotelbooking.validator.DateValidator;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
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

    private final EntityValidator entityValidator;
    private final DateValidator dateValidator;

    // List các trạng thái Booking đang giữ phòng
    private static final List<BookingStatus> ACTIVE_HOLDING_STATUSES = List.of(
            BookingStatus.PENDING,
            BookingStatus.PAID,
            BookingStatus.CONFIRMED,
            BookingStatus.CHECKED_IN
    );

    Instant now = Instant.now();

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
     * Insert Booking mới cùng các class liên quan vào DB
     * <p>
     * NOTE: MongoDB không có row-level locking như RDBMS. @Transactional ở đây
     * đảm bảo rollback toàn bộ nếu 1 write giữa chừng thất bại (yêu cầu
     * replica set để multi-document transaction hoạt động).
     */
    @Transactional
    public BookingResponse createNewBooking(CreateBookingRequest request, String userId, String username) {
        // Kiểm tra tồn tại của userId trong DB
        entityValidator.requireUserByUserId(userId);

        // Get các list BookingItems từ request
        List<CreateBookingItemRequest> bookingItemList = request.items();

        // Khởi tạo các map sẽ sử dụng để đkí DB
        List<BookingItem> insertBookingItemList = new ArrayList<>();
        List<BookingItemResponse> bookingItemResponseList = new ArrayList<>();

        // Mỗi phần tử ứng với danh sách roomId sẽ gán cho BookingItem cùng index.
        List<List<String>> roomIdsPerBookingItem = new ArrayList<>();

        // Map lưu trữ các RoomType hợp lệ theo RoomTypeId
        Map<String, RoomType> eligibleRoomTypeMap = new HashMap<>();

        // Số tiền tổng của toàn bộ BookingItems
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Lấy check-in date và check-out date của booking đó
        LocalDate checkInDate = request.checkInDate();
        LocalDate checkOutDate = request.checkOutDate();

        // Kiểm tra ngày check in và check out
        dateValidator.validateCheckInOutDate(checkInDate, checkOutDate);

        // Lấy bookingId của các Booking còn hiệu lực
        Set<String> validBookingIds = getValidBookingIds(checkInDate, checkOutDate);

        // Lấy BookingItem đang overlap và booking còn hiệu lực
        List<String> occupiedBookingItemsIds =
                bookingItemRepository.findByBookingIdInAndDeleteFlagFalse(validBookingIds)
                        .stream()
                        .map(BookingItem::getId)
                        .toList();

        // Tìm Room đang bị chiếm
        Set<String> occupiedRoomIds = new HashSet<>();
        occupiedRoomIds.addAll(findOccupiedRoomIds(occupiedBookingItemsIds));

        // Get thời gian thực thi hiện tại
        Instant now = Instant.now();

        // Khởi tạo Set lưu lại các roomId đã chọn trong lúc duyệt qua từng bookingItem
        Set<String> reservedInThisRequest = new HashSet<>();

        // Bắt đầu duyệt qua từng bookingItem
        for (CreateBookingItemRequest bookingItem : bookingItemList) {

            String roomTypeId = bookingItem.roomTypeId();
            Integer bookingItemRoomCnt = bookingItem.quantity();

            // 1. Lấy RoomType phù hợp: deleteFlag = false, status = ACTIVE
            RoomType eligibleRoomType = entityValidator.requireRoomType(roomTypeId, RoomTypeStatus.ACTIVE);

            // TH giá Room Type từ DB đang sai khác so với giá của Booking
            // --> Báo lỗi Conflict
            if (eligibleRoomType.getPrice().compareTo(bookingItem.price()) != 0) {
                throw new ConflictException("There's a Room Type that has just updated its Price. Please refresh the Booking");
            }

            // Đặt roomType theo roomTypeId vào map các roomType hợp lệ
            eligibleRoomTypeMap.put(eligibleRoomType.getId(), eligibleRoomType);

            // List bổ sung thêm các Room đã đặt ở lượt duyệt bookingItem trước
            occupiedRoomIds.addAll(reservedInThisRequest);

            // 4. Lấy Room còn khả dụng: deleteFlag = false, status = ACTIVE,
            // roomTypeId thuộc danh sách eligible, id không nằm trong occupiedRoomIds
            List<Room> availableRoomsList = findAvailableRooms(RoomStatus.ACTIVE, roomTypeId, occupiedRoomIds);

            // TH không tồn tại phòng nào có thể đặt
            if (availableRoomsList.size() < bookingItem.quantity()) {
                throw new ConflictException(
                        "The following room type no longer have available room: " +
                                eligibleRoomType.getRoomTypeName());
            }

            // Cộng số tiền booking của loại phòng đó vào tổng tiền của cả Booking
            totalAmount = totalAmount.add(calculateTotalBookingMoney(
                    checkInDate,
                    checkOutDate,
                    eligibleRoomType.getPrice(),
                    bookingItemRoomCnt));

            // Tạo BookingItem
            insertBookingItemList.add(createBookingItem(username,
                    roomTypeId,
                    bookingItemRoomCnt,
                    eligibleRoomType.getPrice()));

            // Get roomId của số lượng các Room có thể chọn dựa trên trị quantity
            List<String> chosenRoomIdList = availableRoomsList.stream()
                    .limit(bookingItemRoomCnt)
                    .map(Room::getId)
                    .toList();

            // Thêm vào Set lưu lại các roomId đã chọn lượt insert BookingItem này
            reservedInThisRequest.addAll(chosenRoomIdList);

            // Thêm vào List để insert các roomId vào RoomAssignment
            roomIdsPerBookingItem.add(chosenRoomIdList);
        }

        // Get ngày tạo Booking và ngày Bookning hết hạn (ngày tạo + 15 phút)
        LocalDateTime dateTimeCreatedAt = now.atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime dateTimeExpiredAt = dateTimeCreatedAt.plusMinutes(15);

        // Đăng ký trước Booking và lấy bookingId
        Booking insertedBooking = insertBooking(userId,
                checkInDate,
                checkOutDate,
                dateTimeExpiredAt,
                username,
                now);

        // Dựa theo trên thì size của insertBookingItemList và insertRoomAssignmentList luôn bằng nhau
        for (int i = 0; i < insertBookingItemList.size(); i++) {
            // Lấy thông tin bookingItem và roomAssignment chuẩn bị insert
            BookingItem bookingItem = insertBookingItemList.get(i);
            List<String> chosenRoomIds = roomIdsPerBookingItem.get(i);

            // Với bookingItem thì gắng bookingId đã được insert trước và insert bookingItem này vào DB
            bookingItem.setBookingId(insertedBooking.getId());
            BookingItem insertedBookingItem = bookingItemRepository.save(bookingItem);

            // Với roomAssignment thì gắng bookingItemId vừa insert xong và insert roomAssignment này vào DB
            for (String roomId : chosenRoomIds) {
                RoomAssignment roomAssignment = createRoomAssignment(roomId, username);
                roomAssignment.setBookingItemId(insertedBookingItem.getId());
                roomAssignmentRepository.save(roomAssignment);
            }

            // Tiếp theo lấy roomTypeId, roomTypeName, và roomPrice hợp lệ từ trước để tạo BookingItem Response
            String roomTypeId = insertedBookingItem.getRoomTypeId();
            String roomTypeName = eligibleRoomTypeMap.get(roomTypeId).getRoomTypeName();
            BookingItemResponse bookingItemResponse = toBookingItemResponse(insertedBookingItem,
                    roomTypeName);
            bookingItemResponseList.add(bookingItemResponse);
        }

        // Tạo Booking Response chứa đầy đủ Response trả về
        return toBookingResponse(insertedBooking,
                bookingItemResponseList,
                checkInDate,
                checkOutDate,
                totalAmount,
                dateTimeCreatedAt);
    }

    /**
     * Tính số tiền booking cho 1 loại phòng
     * tổng = [tiền phòng mặc định * (số đêm ngủ * số phòng)]
     */
    private BigDecimal calculateTotalBookingMoney(LocalDate checkInDate, LocalDate checkOutDate,
                                                  BigDecimal price, Integer roomQuantity) {
        // Tính số đêm
        long countNight = calculateNights(checkInDate, checkOutDate);
        // Trả về số tiền booking cho 1 loại phòng
        return price.multiply(BigDecimal.valueOf(countNight * roomQuantity));
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
    private Booking findBookingByIdAndUserId(String bookingId, String userId) {
        // Kiểm tra sự tồn tại của userId trong DB
        entityValidator.requireUserByUserId(userId);

        // Tìm Booking dựa theo bookingId
        Booking booking = entityValidator.requireBookingByUserId(bookingId, userId);

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
     * Lấy 1 Set các BookingIds đang hữu hiệu
     */
    private Set<String> getValidBookingIds(LocalDate checkInDate, LocalDate checkOutDate) {
        // Thoả mãn toàn bộ các điều kiện sau:
        // 1. Status phải hữu hiệu
        // 2. existing.checkIn <= requestedCheckOut
        // 3. existing.checkOut >= requestedCheckIn
        // Cuối cùng lấy ra các bookingIds và bỏ vào Set
        return bookingRepository
                .findByDeleteFlagFalseAndStatusInAndCheckInTimeLessThanAndCheckOutTimeGreaterThan(
                        ACTIVE_HOLDING_STATUSES,
                        checkOutDate,
                        checkInDate)
                .stream()
                .map(Booking::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Lấy 1 Set các roomIds đã được đặt dựa trên bookingItemIds
     */
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

    /**
     * Tìm các phòng đang ACTIVE, theo RoomTypeId cho trước, và
     * nằm ngoài các roomIds đã được book trước
     */
    private List<Room> findAvailableRooms(RoomStatus roomStatus, String roomTypeId,
                                          Set<String> occupiedRoomIds) {
        return roomRepository.findByDeleteFlagFalseAndStatusAndRoomTypeIdAndIdNotIn(
                roomStatus,
                roomTypeId,
                occupiedRoomIds,
                Sort.by(Sort.Direction.ASC, "roomNumber")
        );
    }

    /*
     * Khởi tạo trước BookingItem
     */
    private BookingItem createBookingItem(String username, String roomTypeId, Integer quantity, BigDecimal price) {
        BookingItem newBookingItem = new BookingItem();

        newBookingItem.setRoomTypeId(roomTypeId);
        newBookingItem.setQuantity(quantity);
        newBookingItem.setPrice(price);
        newBookingItem.setDeleteFlag(false);
        newBookingItem.setCreatedBy(username);
        newBookingItem.setCreatedAt(now);
        newBookingItem.setUpdatedBy(null);
        newBookingItem.setUpdatedAt(null);

        return newBookingItem;
    }

    private Booking insertBooking(String userId,
                                  LocalDate checkInDate,
                                  LocalDate checkOutDate,
                                  LocalDateTime dateTimeExpiredAt,
                                  String username,
                                  Instant now) {
        Booking newBooking = new Booking();

        newBooking.setUserId(userId);
        newBooking.setStatus(BookingStatus.PENDING);
        newBooking.setCheckInDate(checkInDate);
        newBooking.setCheckOutDate(checkOutDate);
        newBooking.setExpiresAt(dateTimeExpiredAt);
        newBooking.setDeleteFlag(false);
        newBooking.setCreatedBy(username);
        newBooking.setCreatedAt(now);
        newBooking.setUpdatedBy(null);
        newBooking.setUpdatedAt(null);

        return bookingRepository.save(newBooking);
    }

    /*
     * Khởi tạo trước RoomAssignment
     */
    private RoomAssignment createRoomAssignment(String roomId, String username) {
        RoomAssignment newRoomAssignment = new RoomAssignment();

        newRoomAssignment.setRoomId(roomId);
        newRoomAssignment.setDeleteFlag(false);
        newRoomAssignment.setCreatedBy(username);
        newRoomAssignment.setCreatedAt(now);
        newRoomAssignment.setUpdatedBy(null);
        newRoomAssignment.setUpdatedAt(null);

        return newRoomAssignment;
    }

}
