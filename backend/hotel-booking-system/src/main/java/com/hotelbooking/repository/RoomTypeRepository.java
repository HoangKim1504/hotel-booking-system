package com.hotelbooking.repository;

import com.hotelbooking.model.RoomType;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomTypeRepository extends MongoRepository<RoomType, String> {

    Optional<RoomType> findByIdAndDeleteFlagFalse(String id);

    List<RoomType> findAllByDeleteFlagFalse();

    Page<RoomType> findAllByDeleteFlagFalse(@NonNull Pageable pageable);

}

