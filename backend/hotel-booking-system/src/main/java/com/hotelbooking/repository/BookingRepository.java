package com.hotelbooking.repository;

import com.hotelbooking.enums.BookingStatus;
import com.hotelbooking.model.Booking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<String> findByDeleteFlagFalseAndStatusIn(List<BookingStatus> statuses);

}
