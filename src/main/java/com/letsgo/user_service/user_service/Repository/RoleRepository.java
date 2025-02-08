package com.letsgo.user_service.user_service.Repository;

import com.letsgo.user_service.user_service.model.Role;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(RoleEnum name);
}
