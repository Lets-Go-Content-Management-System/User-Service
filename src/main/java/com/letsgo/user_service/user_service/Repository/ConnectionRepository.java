package com.letsgo.user_service.user_service.Repository;


import com.letsgo.user_service.user_service.model.Connection;
import com.letsgo.user_service.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, UUID> {

    // Find all followers of a user
    List<Connection> findByFollowing(User following);

    // Find all users a user is following
    List<Connection> findByFollower(User follower);

    // Check if a user follows another user
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Remove a connection (unfollow a user)
    void deleteByFollowerAndFollowing(User follower, User following);

}
