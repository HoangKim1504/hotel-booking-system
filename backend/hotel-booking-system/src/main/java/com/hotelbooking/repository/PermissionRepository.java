package com.hotelbooking.repository;

import com.hotelbooking.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends MongoRepository<Permission, String> {

    Optional<Permission> findByCode(String code);

    List<Permission> findByIdIn(Collection<String> ids);

}
