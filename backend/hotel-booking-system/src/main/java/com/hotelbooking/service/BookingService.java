package com.hotelbooking.service;

import com.hotelbooking.dto.PageResponse;
import com.hotelbooking.dto.SimpleBookingResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingItem;
import com.hotelbooking.repository.BookingItemRepository;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.utils.PageableUtils;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    private final EntityValidator entityValidator;

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
     * Lấy danh sách Booking của user đã chọn
     */
    public PageResponse<SimpleBookingResponse> getBookingListOfUser(int page, int size, BookingStatus bookingStatus, String userId) {
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


}
