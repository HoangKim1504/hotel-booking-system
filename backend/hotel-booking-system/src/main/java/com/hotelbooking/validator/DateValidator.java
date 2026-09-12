package com.hotelbooking.validator;

import com.hotelbooking.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DateValidator {

    /**
     * Check ngày check-in phải trước ngày check-out.
     */
    public void validateCheckInOutDate(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new BadRequestException("checkInDate", "Check-in date cannot be in the past");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new BadRequestException("checkOutDate", "Check-out date must be after check-in date");
        }
    }

}
