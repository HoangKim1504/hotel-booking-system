package com.hotelbooking.service;

import com.hotelbooking.dto.payment.CreatePaymentRequest;
import com.hotelbooking.dto.payment.PaymentResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.PaymentStatus;
import com.hotelbooking.exception.ConflictException;
import com.hotelbooking.exception.ForbiddenException;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.BookingItem;
import com.hotelbooking.model.Payment;
import com.hotelbooking.repository.BookingItemRepository;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.PaymentRepository;
import com.hotelbooking.utils.DateUtils;
import com.hotelbooking.validator.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingItemRepository bookingItemRepository;
    private final BookingRepository bookingRepository;

    private final EntityValidator entityValidator;

    private static final String CASH_PAYMENT = "CASH";

    /**
     * Tạo payment cho Booking.
     */
    @Transactional
    public PaymentResponse createPayment(
            String bookingId,
            CreatePaymentRequest request,
            String userId,
            String username,
            boolean adminFlag
    ) {
        // 1. Kiểm tra booking tồn tại không
        Booking booking = entityValidator.requireBooking(bookingId);

        // 2. Check booking thuộc user đang login (chức năng cho user)
        if (!booking.getUserId().equals(userId) && !adminFlag) {
            throw new ForbiddenException("You cannot pay for this booking");
        }

        // 3. Chỉ booking pending mới tạo payment
        if (!BookingStatus.PENDING.equals(booking.getStatus())) {
            throw new ConflictException("Booking is not available for payment");
        }

        // 4. Check booking hết hạn
        LocalDateTime now = LocalDateTime.now();
        if (booking.getExpiresAt() == null || !booking.getExpiresAt().isAfter(now)) {
            throw new ConflictException("Booking has expired");
        }

        // 5. Check payment tồn tại
        if (paymentRepository.existsByBookingIdAndDeleteFlagFalse(bookingId)) {
            throw new ConflictException("Payment already exists for this booking");
        }

        // 6. Tính total amount từ booking item
        BigDecimal totalAmount = calculateBookingTotal(booking);

        // 7. Tạo payment
        Instant currentInstant = Instant.now();
        Payment payment =
                Payment.builder()
                        .bookingId(bookingId)
                        .amount(totalAmount)
                        .paymentMethod(request.paymentMethod().toUpperCase())
                        .build();

        // 8. Xử lý theo payment method
        if (CASH_PAYMENT.equals(request.paymentMethod()) && adminFlag) {
            handleAdminCashPayment(payment, booking, username, currentInstant);
        } else if (CASH_PAYMENT.equals(request.paymentMethod())) {
            handleUserCashPayment(payment, booking, username, currentInstant);
        } else {
            handleOnlinePayment(payment, booking, username, currentInstant);
        }

        // 9. Save Payment + Booking
        Payment insertedPayment = paymentRepository.save(payment);
        bookingRepository.save(booking);

        // 10. Response
        return toPaymentResponse(insertedPayment);
    }

    private BigDecimal calculateBookingTotal(Booking booking) {
        List<BookingItem> bookingItems =
                bookingItemRepository.findByDeleteFlagFalseAndBookingId(booking.getId());

        long numberOfNights =
                ChronoUnit.DAYS.between(
                        booking.getCheckInDate(),
                        booking.getCheckOutDate()
                );

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (BookingItem item : bookingItems) {
            BigDecimal numberOfNightsFormat = BigDecimal.valueOf(numberOfNights);
            BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());

            BigDecimal itemAmount =
                    item.getPrice()
                            .multiply(numberOfNightsFormat)
                            .multiply(quantity);

            totalAmount = totalAmount.add(itemAmount);
        }

        return totalAmount;
    }

    private void handleAdminCashPayment(Payment payment, Booking booking, String username, Instant currentInstant) {
        /*
         * Khách hàng luôn trả tiền tại khách sạn khi check-in.
         */
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(DateUtils.toLocalDateTime(currentInstant));
        payment.setTransactionId(null);
        payment.setDeleteFlag(false);
        payment.setCreatedBy(username);
        payment.setCreatedAt(currentInstant);
        payment.setUpdatedBy(null);
        payment.setUpdatedAt(null);

        // Booking đã được xác nhận giữ phòng.
        booking.setStatus(BookingStatus.PAID);
        // Đã thanh toán thành công nên không còn expire
        booking.setExpiresAt(null);
        booking.setUpdatedBy(username);
        booking.setUpdatedAt(currentInstant);
    }

    private void handleUserCashPayment(Payment payment, Booking booking, String username, Instant currentInstant) {
        /*
         * User chọn trả tiền tại khách sạn.
         * Chưa nhận được tiền -> Payment vẫn PENDING.
         */
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(null);
        payment.setTransactionId(null);
        payment.setDeleteFlag(false);
        payment.setCreatedBy(username);
        payment.setCreatedAt(currentInstant);
        payment.setUpdatedBy(null);
        payment.setUpdatedAt(null);

        // Booking đã được xác nhận giữ phòng.
        booking.setStatus(BookingStatus.CONFIRMED);
        // Booking đã confirmed nên không còn timeout thanh toán 15 phút nữa.
        booking.setExpiresAt(null);
        booking.setUpdatedBy(username);
        booking.setUpdatedAt(currentInstant);
    }

    private void handleOnlinePayment(Payment payment, Booking booking, String username, Instant paymentDate) {
        /*
         * Hiện tại đang MOCK online payment thành công.
         */
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentDate(DateUtils.toLocalDateTime(paymentDate));
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setDeleteFlag(false);
        payment.setCreatedBy(username);
        payment.setCreatedAt(paymentDate);
        payment.setUpdatedBy(null);
        payment.setUpdatedAt(null);

        // Booking đã được xác nhận giữ phòng.
        booking.setStatus(BookingStatus.PAID);
        // Đã thanh toán thành công nên không còn expire
        booking.setExpiresAt(null);
        booking.setUpdatedBy(username);
        booking.setUpdatedAt(paymentDate);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getPaymentDate(),
                payment.getTransactionId()
        );
    }

}
