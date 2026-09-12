package com.hotelbooking.dto.payment;

import com.hotelbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        String id,

        String bookingId,

        BigDecimal amount,

        String paymentMethod,

        PaymentStatus status,

        LocalDateTime paymentDate,

        String transactionId

) {
}
