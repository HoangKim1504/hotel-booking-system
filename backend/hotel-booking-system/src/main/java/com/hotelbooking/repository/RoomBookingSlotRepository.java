package com.hotelbooking.repository;

import com.hotelbooking.model.RoomBookingSlot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomBookingSlotRepository extends MongoRepository<RoomBookingSlot, String> {

    void deleteByBookingId(String bookingId);
    
}
