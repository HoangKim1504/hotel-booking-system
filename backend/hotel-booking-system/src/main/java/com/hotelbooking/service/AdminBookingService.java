package com.hotelbooking.service;

import com.hotelbooking.dto.*;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingItem;
import com.hotelbooking.model.RoomAssignment;
import com.hotelbooking.repository.BookingItemRepository;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomAssignmentRepository;
import com.hotelbooking.repository.RoomBookingSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * SERVICE — CRUD Booking.
 *
 * <p>Phân quyền API nằm ở {@code @PreAuthorize} trên Controller — service không
 * hardcode tên role để cho phép/từ chối gọi API.</p>
 */
@Service
@RequiredArgsConstructor
public class AdminBookingService {

    private final BookingService bookingService;

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final RoomAssignmentRepository roomAssignmentRepository;
    private final RoomBookingSlotRepository roomBookingSlotRepository;

    // List các trạng thái Booking không thể bị Delete
    private static final List<BookingStatus> CANNOT_BE_DELETED_STATUSES = List.of(
            BookingStatus.PAID,
            BookingStatus.CHECKED_IN,
            BookingStatus.CONFIRMED,
            BookingStatus.COMPLETED,
            BookingStatus.REFUNDED
    );

    public PageResponse<SimpleBookingResponse> getBookingsForAdmin(
            int page,
            int size,
            BookingStatus bookingStatus
    ) {
        return bookingService.getBookingList(
                page,
                size,
                bookingStatus,
                null
        );
    }

    public BookingResponse getBookingDetailForAdmin(
            String id,
            String userId
    ) {
        return bookingService.getBookingDetail(
                id,
                userId
        );
    }

    public BookingResponse createNewBookingForAdmin(
            CreateBookingRequest request,
            String userId,
            String username
    ) {
        return bookingService.createNewBooking(
                request,
                userId,
                username
        );
    }

    public UpdateBookingResponse cancelBookingForAdmin(
            String bookingId,
            String userId,
            String username
    ) {
        return bookingService.cancelBooking(
                bookingId,
                userId,
                username
        );
    }

    public void deleteBooking(String bookingId, String userId, String username) {
        // 1. Tìm Booking của user hiện tại
        Booking booking = bookingService.findBookingByIdAndUserId(bookingId, userId);

        // 2. Validate status có được delete hay không
        validateBookingCanBeDeleted(booking);

        Instant now = Instant.now();

        // 3. Soft-delete Booking
        booking.setDeleteFlag(true);
        booking.setUpdatedBy(username);
        booking.setUpdatedAt(now);

        bookingRepository.save(booking);

        // 4. Lấy BookingItem
        List<BookingItem> bookingItems =
                bookingItemRepository.findByDeleteFlagFalseAndBookingId(bookingId);

        List<String> bookingItemIds =
                bookingItems.stream()
                        .map(BookingItem::getId)
                        .toList();

        // 5. Soft-delete BookingItem
        for (BookingItem item : bookingItems) {
            item.setDeleteFlag(true);
            item.setUpdatedBy(username);
            item.setUpdatedAt(now);
        }

        bookingItemRepository.saveAll(bookingItems);

        // 6. Soft-delete RoomAssignment
        if (!bookingItemIds.isEmpty()) {
            List<RoomAssignment> assignments =
                    roomAssignmentRepository.findByDeleteFlagFalseAndBookingItemIdIn(bookingItemIds);
            for (RoomAssignment assignment : assignments) {
                assignment.setDeleteFlag(true);
                assignment.setUpdatedBy(username);
                assignment.setUpdatedAt(now);
            }
            roomAssignmentRepository.saveAll(assignments);
        }

        // 7. Release RoomBookingSlot
        roomBookingSlotRepository.deleteByBookingId(bookingId);
    }

    private void validateBookingCanBeDeleted(Booking booking) {
        BookingStatus currentStatus = booking.getStatus();

        if (CANNOT_BE_DELETED_STATUSES.contains(currentStatus)) {
            throw new ConflictException("Booking with status " + currentStatus + " cannot be deleted");
        }
    }

}
