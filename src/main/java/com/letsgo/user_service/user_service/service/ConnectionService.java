package com.letsgo.user_service.user_service.service;


import com.letsgo.user_service.user_service.Repository.ConnectionRepository;
import com.letsgo.user_service.user_service.dto.CreateUserResponseDto;
import com.letsgo.user_service.user_service.model.Connection;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConnectionService {

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private UserService userService;

    // Follow a user
    public Connection followUser (UUID followerId, UUID followingId) {
        Optional<User> follower = userService.getUserById(followerId);
        Optional<User> following = userService.getUserById(followingId);

        if (follower.isPresent() && following.isPresent() && !connectionRepository.existsByFollowerAndFollowing(follower.get(), following.get())) {
            Connection connection = new Connection();
            connection.setFollower(follower.get());
            connection.setFollowing(following.get());
            return connectionRepository.save(connection);
        }

        return null;
    }

    // Unfollow a user

    @Transactional
    public void unfollowUser(UUID followerId, UUID followingId) {
        Optional<User> follower = userService.getUserById(followerId);
        Optional<User> following = userService.getUserById(followingId);

        if (follower.isPresent() && following.isPresent()) {
            connectionRepository.deleteByFollowerAndFollowing(follower.get(), following.get());
        }
    }


    // Check if a user follows another user
    public boolean isFollowing(UUID followerId, UUID followingId) {
        Optional<User> follower = userService.getUserById(followerId);
        Optional<User> following = userService.getUserById(followingId);

        return follower.isPresent() && following.isPresent() &&
                connectionRepository.existsByFollowerAndFollowing(follower.get(), following.get());
    }

    // Get all followers of a user
//    public List<Connection> getAllFollowers(UUID userId) {
//        Optional<User> user = userService.getUserById(userId);
//        return user.map(connectionRepository::findByFollowing).orElse(List.of());
//    }

    public List<CreateUserResponseDto> getAllFollowers(UUID userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> connectionRepository.findByFollowing(u).stream()
                        .map(connection -> new CreateUserResponseDto(
                                connection.getFollower().getId(),
                                connection.getFollower().getEmail(),
                                connection.getFollower().getFirstName(),
                                connection.getFollower().getLastName(),
                                connection.getFollower().getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()),
                                null // Token is not available here; set to null or fetch it if needed
                                ))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }


    public List<CreateUserResponseDto> getFollowing(UUID userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> connectionRepository.findByFollower(u).stream()
                        .map(connection -> new CreateUserResponseDto(
                                connection.getFollowing().getId(),
                                connection.getFollowing().getEmail(),
                                connection.getFollowing().getFirstName(),
                                connection.getFollowing().getLastName(),
                                connection.getFollowing().getRoles().stream().map(role -> role.getName()).collect(Collectors.toSet()),
                                null
                                ))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // Get all users a user is following
//    public List<Connection> getFollowing(UUID userId) {
//        Optional<User> user = userService.getUserById(userId);
//        return user.map(connectionRepository::findByFollower).orElse(List.of());
//    }

}
