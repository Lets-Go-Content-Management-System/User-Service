package com.letsgo.user_service.user_service.Repository;

import com.letsgo.user_service.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findById(UUID id);
    void deleteById(UUID id);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
