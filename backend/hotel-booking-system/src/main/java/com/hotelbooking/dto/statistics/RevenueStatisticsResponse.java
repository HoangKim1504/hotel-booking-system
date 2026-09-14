package com.hotelbooking.dto.statistics;

import java.math.BigDecimal;
import java.util.List;

public record RevenueStatisticsResponse(
        Integer year,
        BigDecimal totalRevenue,
        Long totalBookings,
        Long successfulPayments,
        Long cancelledBookings,
        List<MonthlyRevenueResponse> monthlyRevenue
) {
}
