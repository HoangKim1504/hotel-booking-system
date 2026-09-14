package com.hotelbooking.service.admin;

import com.hotelbooking.dto.statistics.MonthlyRevenueResponse;
import com.hotelbooking.dto.statistics.RevenueStatisticsResponse;
import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.enums.PaymentStatus;
import com.hotelbooking.model.Booking;
import com.hotelbooking.model.Payment;
import com.hotelbooking.repository.BookingRepository;
import com.hotelbooking.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStatisticsService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final MongoTemplate mongoTemplate;

    public RevenueStatisticsResponse getRevenueStatistics(
            int year) {

        LocalDate bookingStartDate =
                LocalDate.of(year, 1, 1);

        LocalDate bookingEndDate =
                bookingStartDate.plusYears(1);

        LocalDateTime paymentStartDate =
                bookingStartDate.atStartOfDay();

        LocalDateTime paymentEndDate =
                paymentStartDate.plusYears(1);

        // Booking query
        Query bookingQuery = new Query();

        bookingQuery.addCriteria(
                Criteria.where("deleteFlag")
                        .is(false)
        );

        bookingQuery.addCriteria(
                Criteria.where("checkInDate")
                        .gte(bookingStartDate)
                        .lt(bookingEndDate)
        );

        List<Booking> bookings =
                mongoTemplate.find(
                        bookingQuery,
                        Booking.class
                );

        // Payment query
        Query paymentQuery = new Query();

        paymentQuery.addCriteria(
                Criteria.where("deleteFlag")
                        .is(false)
        );

        paymentQuery.addCriteria(
                Criteria.where("status")
                        .is(PaymentStatus.SUCCESS)
        );

        paymentQuery.addCriteria(
                Criteria.where("paymentDate")
                        .gte(paymentStartDate)
                        .lt(paymentEndDate)
        );

        List<Payment> successfulPayments =
                mongoTemplate.find(
                        paymentQuery,
                        Payment.class
                );

        // 4. Count bookings
        long totalBookings = bookings.size();

        // 5. Count cancelled bookings
        long cancelledBookings =
                bookings.stream()
                        .filter(booking ->
                                booking.getStatus()
                                        == BookingStatus.CANCELLED
                        )
                        .count();

        // 6. Calculate total revenue
        BigDecimal totalRevenue =
                successfulPayments.stream()
                        .map(Payment::getAmount)
                        .filter(amount -> amount != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        // 7. Calculate monthly revenue
        List<MonthlyRevenueResponse> monthlyRevenue =
                calculateMonthlyRevenue(
                        successfulPayments
                );

        return new RevenueStatisticsResponse(
                year,
                totalRevenue,
                totalBookings,
                (long) successfulPayments.size(),
                cancelledBookings,
                monthlyRevenue
        );
    }

    /**
     * Calculate revenue for every month from January to December.
     */
    private List<MonthlyRevenueResponse> calculateMonthlyRevenue(
            List<Payment> payments) {

        // Initialize 12 months with zero revenue
        List<BigDecimal> monthlyTotals =
                new ArrayList<>();

        for (int month = 0; month < 12; month++) {
            monthlyTotals.add(
                    BigDecimal.ZERO
            );
        }

        // Sum payment amount by month
        for (Payment payment : payments) {

            if (payment.getPaymentDate() == null
                    || payment.getAmount() == null) {
                continue;
            }

            int monthIndex =
                    payment.getPaymentDate()
                            .getMonthValue() - 1;

            BigDecimal currentRevenue =
                    monthlyTotals.get(monthIndex);

            monthlyTotals.set(
                    monthIndex,
                    currentRevenue.add(
                            payment.getAmount()
                    )
            );
        }

        // Convert to response
        List<MonthlyRevenueResponse> result =
                new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            result.add(
                    new MonthlyRevenueResponse(
                            month,
                            monthlyTotals.get(month - 1)
                    )
            );
        }

        return result;
    }
}