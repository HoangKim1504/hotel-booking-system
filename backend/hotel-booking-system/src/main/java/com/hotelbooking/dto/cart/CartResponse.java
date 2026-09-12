package com.hotelbooking.dto.cart;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        String id,
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        List<String> warnings) {
}
