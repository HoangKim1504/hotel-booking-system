package com.hotelbooking.service.admin;

import com.hotelbooking.dto.booking.BookingResponse;
import com.hotelbooking.dto.booking.CreateBookingRequest;
import com.hotelbooking.dto.booking.SimpleBookingResponse;
import com.hotelbooking.dto.booking.UpdateBookingResponse;
import com.hotelbooking.dto.common.PageResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingItem;
import com.hotelbooking.model.RoomAssignment;
import com.hotelbooking.repository.BookingItemRepository;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.RoomAssignmentRepository;
import com.hotelbooking.repository.RoomBookingSlotRepository;
import com.hotelbooking.service.BookingService;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private final EntityValidator entityValidator;

    // List các trạng thái Booking không thể bị Delete
    private static final List<BookingStatus> CANNOT_BE_DELETED_STATUSES = List.of(
            BookingStatus.PAID,
            BookingStatus.CHECKED_IN,
            BookingStatus.CONFIRMED,
            BookingStatus.COMPLETED,
            BookingStatus.REFUNDED
    );

    // List các trạng thái Booking không thể bị đổi status
    private static final List<BookingStatus> CANNOT_BE_CHANGED_STATUSES = List.of(
            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED,
            BookingStatus.EXPIRED,
            BookingStatus.REFUNDED
    );

    // List các trường hợp Booking có thể đổi status
    private static final Map<BookingStatus, Set<BookingStatus>>
            ALLOWED_STATUS_TRANSITIONS = Map.of(
            // Flow: PENDING -> PAID; PENDING -> CONFIRMED
            BookingStatus.PENDING,
            Set.of(BookingStatus.PAID, BookingStatus.CONFIRMED),
            // Flow: PAID -> CONFIRMED
            BookingStatus.PAID,
            Set.of(BookingStatus.CONFIRMED),
            // Flow: CONFIRMED -> CHECKED_IN
            BookingStatus.CONFIRMED,
            Set.of(BookingStatus.CHECKED_IN),
            // Flow: CHECKED_IN -> COMPLETED
            BookingStatus.CHECKED_IN,
            Set.of(BookingStatus.COMPLETED)
    );

    private static final Set<BookingStatus> RELEASE_ROOM_STATUSES = Set.of(
            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED,
            BookingStatus.EXPIRED,
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

    @Transactional
    public UpdateBookingResponse updateBookingStatus(String bookingId, String userId,
                                                     BookingStatus newStatus, String username) {
        // 1. Tìm Booking của user hiện tại
        Booking booking = entityValidator.requireBookingOwnedByUser(bookingId, userId);

        // 2. Validate status có được đổi hay không
        validateBookingCanBeChangedStatus(booking);

        // 3. Check flow currentStatus -> newStatus
        validateBookingStatusFlowCanBeChanged(booking, newStatus);

        // 4. Update status
        Instant now = Instant.now();

        booking.setStatus(newStatus);
        booking.setUpdatedBy(username);
        booking.setUpdatedAt(now);

        Booking updatedBooking = bookingRepository.save(booking);

        // 5. Nếu Booking không còn giữ phòng thì release RoomBookingSlot
        releaseRoomsIfNeeded(newStatus, bookingId, username, now);

        // 5. Return response
        return bookingService.toUpdateBookingResponse(updatedBooking);
    }

    @Transactional
    public void deleteBooking(String bookingId, String userId, String username) {
        // 1. Tìm Booking của user hiện tại
        Booking booking = entityValidator.requireBookingOwnedByUser(bookingId, userId);

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

    private void validateBookingCanBeChangedStatus(Booking booking) {
        BookingStatus currentStatus = booking.getStatus();

        if (CANNOT_BE_CHANGED_STATUSES.contains(currentStatus)) {
            throw new ConflictException(
                    "Booking with status " + currentStatus + " cannot be changed"
            );
        }
    }

    private void validateBookingStatusFlowCanBeChanged(Booking booking, BookingStatus newStatus) {
        BookingStatus currentStatus = booking.getStatus();

        // Không cần update nếu status giống hiện tại
        if (currentStatus == newStatus) {
            throw new ConflictException("Booking is already in status: " + currentStatus);
        }

        Set<BookingStatus> allowedStatuses =
                ALLOWED_STATUS_TRANSITIONS.getOrDefault(
                        currentStatus,
                        Set.of()
                );

        if (!allowedStatuses.contains(newStatus)) {
            throw new ConflictException(
                    "Booking status cannot be changed from " + currentStatus + " to " + newStatus
            );
        }
    }

    private void releaseRoomsIfNeeded(BookingStatus newStatus, String bookingId, String username, Instant now) {
        if (!RELEASE_ROOM_STATUSES.contains(newStatus)) {
            return;
        }

        roomBookingSlotRepository.deleteByBookingId(bookingId);

        bookingService.releaseRoomAssignments(bookingId, username, now);
    }


}
