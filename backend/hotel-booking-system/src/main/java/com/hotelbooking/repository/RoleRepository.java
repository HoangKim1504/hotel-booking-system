package com.hotelbooking.repository;

import com.hotelbooking.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    List<Role> findByIdInAndDeleteFlagFalse(Collection<String> ids);

    Optional<Role> findByCodeAndDeleteFlagFalse(String code);

}
