package com.hotelbooking.service;

import com.hotelbooking.dto.CreatePaymentRequest;
import com.hotelbooking.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {

    private final PaymentService paymentService;

    @Transactional
    public PaymentResponse createPaymentForAdmin(
            String bookingId,
            CreatePaymentRequest request,
            String userId,
            String username,
            boolean adminFlag
    ) {
        return paymentService.createPayment(
                bookingId,
                request,
                userId,
                username,
                adminFlag);
    }

}
