package com.hotelbooking.repository;

import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByDeleteFlagFalseAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            LocalDate checkOutDate, LocalDate checkInDate);

    List<Booking> findByDeleteFlagFalseAndUserId(String userId);

    List<Booking> findByDeleteFlagFalseAndUserIdAndStatus(String userId, BookingStatus status);

    List<Booking> findByDeleteFlagFalse();

    List<Booking> findByDeleteFlagFalseAndStatus(BookingStatus status);

    Optional<Booking> findByDeleteFlagFalseAndId(String bookingId);

    List<Booking> findByDeleteFlagFalseAndStatusInAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Collection<BookingStatus> statuses,
            LocalDate checkInDate,
            LocalDate checkOutDate);

    List<Booking> findByDeleteFlagFalseAndStatusIn(List<BookingStatus> statuses);

}
