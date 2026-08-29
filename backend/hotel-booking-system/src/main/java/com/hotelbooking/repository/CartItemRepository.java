package com.hotelbooking.repository;

import com.hotelbooking.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends MongoRepository<CartItem, String> {

    List<CartItem> findByCartIdAndDeleteFlagFalse(String cartId);

    Optional<CartItem> findByCartIdAndRoomTypeIdAndDeleteFlagFalse(String cartId, String roomTypeId);

    Optional<CartItem> findByIdAndDeleteFlagFalse(String id);

}
