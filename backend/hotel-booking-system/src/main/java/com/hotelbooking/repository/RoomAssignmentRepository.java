package com.hotelbooking.repository;

import com.hotelbooking.model.RoomAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface RoomAssignmentRepository extends MongoRepository<RoomAssignment, String> {

    List<RoomAssignment> findByDeleteFlagFalseAndBookingItemIdIn(Collection<String> bookingItemIds);

    List<RoomAssignment> findByDeleteFlagFalseAndRoomIdIn(Collection<String> bookingItemIds);

    boolean existsByRoomIdAndDeleteFlagFalse(String roomId);

}
