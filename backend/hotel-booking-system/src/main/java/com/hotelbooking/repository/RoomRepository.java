package com.hotelbooking.repository;

import com.hotelbooking.enums.RoomStatus;
import com.hotelbooking.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoomRepository extends MongoRepository<Room, String> {

    List<Room> findByDeleteFlagFalseAndStatusAndRoomTypeIdInAndIdNotIn(
            RoomStatus roomStatus, List<String> eligibleRoomTypeIds, Set<String> occupiedRoomIds);

    List<Room> findByRoomTypeIdAndDeleteFlagFalse(String id);

    Optional<Room> findByIdAndDeleteFlagFalse(String id);

}
