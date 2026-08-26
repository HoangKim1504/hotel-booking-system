package com.hotelbooking.repository;

import com.hotelbooking.model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CartItemRepository extends MongoRepository<CartItem, String> {

    List<CartItem> findAllByDeleteFlagFalse();

}
