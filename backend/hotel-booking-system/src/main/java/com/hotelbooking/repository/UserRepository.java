package com.hotelbooking.repository;

import com.hotelbooking.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByIdAndDeleteFlagFalse(String id);

    List<User> findAllByDeleteFlagFalse();

    Optional<User> findByUsernameAndDeleteFlagFalse(String username);

}
