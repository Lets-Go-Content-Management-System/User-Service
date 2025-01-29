package com.letsgo.user_service.user_service.service;


import com.letsgo.user_service.user_service.Repository.ConnectionRepository;
import com.letsgo.user_service.user_service.dto.UserCreateDTO;
import com.letsgo.user_service.user_service.dto.UserResponseDTO;
import com.letsgo.user_service.user_service.model.Connection;
import com.letsgo.user_service.user_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
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

    public List<UserResponseDTO> getAllFollowers(UUID userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> connectionRepository.findByFollowing(u).stream()
                        .map(connection -> new UserResponseDTO(
                                connection.getFollower().getId(),
                                connection.getFollower().getFullName(),
                                connection.getFollower().getEmail(),
                                connection.getFollower().getRole(),
                                connection.getFollower().getCreatedAt(),
                                connection.getFollower().getUpdatedAt()))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }


    // Get all users a user is following
    public List<UserResponseDTO> getFollowing(UUID userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.map(u -> connectionRepository.findByFollower(u).stream()
                        .map(connection -> new UserResponseDTO(
                                connection.getFollowing().getId(),
                                connection.getFollowing().getFullName(),
                                connection.getFollowing().getEmail(),
                                connection.getFollower().getRole(),
                                connection.getFollower().getCreatedAt(),
                                connection.getFollower().getUpdatedAt()))
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    // Get all users a user is following
//    public List<Connection> getFollowing(UUID userId) {
//        Optional<User> user = userService.getUserById(userId);
//        return user.map(connectionRepository::findByFollower).orElse(List.of());
//    }

}
