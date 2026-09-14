package com.hotelbooking.controller.admin;

import com.hotelbooking.dto.statistics.RevenueStatisticsResponse;
import com.hotelbooking.service.admin.AdminStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final AdminStatisticsService adminStatisticsService;

    /**
     * Get revenue statistics by year.
     */
    @GetMapping("/revenue")
    @PreAuthorize("hasAuthority('ADMIN_VIEW')")
    public RevenueStatisticsResponse getRevenueStatistics(
            @RequestParam int year) {

        return adminStatisticsService.getRevenueStatistics(year);
    }
}