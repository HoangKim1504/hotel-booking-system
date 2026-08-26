package com.hotelbooking.repository;

import com.hotelbooking.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserIdAndDeleteFlagFalse(String userId);

    boolean existsById(String id);

}
