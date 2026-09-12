package com.hotelbooking.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        String id,
        String roomTypeId,
        String roomTypeName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal) {
}
