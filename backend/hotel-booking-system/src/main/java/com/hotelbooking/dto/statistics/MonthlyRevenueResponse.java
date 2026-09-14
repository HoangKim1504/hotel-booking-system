package com.hotelbooking.dto.statistics;

import java.math.BigDecimal;

public record MonthlyRevenueResponse(
        Integer month,
        BigDecimal revenue
) {
}
