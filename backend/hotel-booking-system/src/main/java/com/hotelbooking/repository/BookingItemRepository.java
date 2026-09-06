package com.hotelbooking.repository;

import com.hotelbooking.model.BookingItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface BookingItemRepository extends MongoRepository<BookingItem, String> {

    List<BookingItem> findByBookingIdInAndDeleteFlagFalse(Collection<String> bookingIds);

}
