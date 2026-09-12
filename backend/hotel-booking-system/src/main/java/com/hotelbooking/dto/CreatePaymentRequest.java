package com.hotelbooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreatePaymentRequest(

        @NotBlank(message = "Payment method is required")
        @Pattern(
                regexp = "(?i)CASH|ONLINE",
                message = "Payment method must be CASH or ONLINE"
        )
        String paymentMethod

) {
}