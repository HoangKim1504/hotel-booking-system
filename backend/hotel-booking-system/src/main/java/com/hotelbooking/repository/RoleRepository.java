package com.hotelbooking.repository;

import com.hotelbooking.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByCode(String code);

    List<Role> findByIdIn(Collection<String> ids);

}
