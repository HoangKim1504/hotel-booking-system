package com.hotelbooking.repository;

import com.hotelbooking.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends MongoRepository<CartItem, String> {

    List<CartItem> findAllByDeleteFlagFalse();

    Optional<CartItem> findByCartIdAndRoomTypeIdAndDeleteFlagFalse(String cartId, String roomTypeId);

}
