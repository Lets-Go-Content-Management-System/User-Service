package com.letsgo.user_service.user_service.controller;


import com.letsgo.user_service.user_service.controller.responses.DefaultResponse;
import com.letsgo.user_service.user_service.dto.CreateUserResponseDto;
import com.letsgo.user_service.user_service.model.Connection;
import com.letsgo.user_service.user_service.service.ConnectionService;
import com.letsgo.user_service.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/connections")
public class ConnectionController {

    @Autowired
    private ConnectionService connectionService;

    @Autowired
    private UserService userService;

    // Follow a user
    @PostMapping("/follow")
    @Operation( summary = "Allows a user to follow another user.")
    @ApiResponse(responseCode = "200", description = "User followed successfully")
    @ApiResponse(responseCode = "400", description = "Unable to follow user")
    public DefaultResponse<Connection> followUser(@RequestParam UUID followerId, @RequestParam UUID followingId) {

        Connection connection = connectionService.followUser(followerId, followingId);
        if (connection != null) {
            return new DefaultResponse<>(connection);
        }
        return new DefaultResponse<>(HttpStatus.BAD_REQUEST.value(), "Unable to follow user", null);
    }

    // Get all users a user is following
    @GetMapping("/following")
    @Operation(summary = "Get all users a user is following")
    @ApiResponse(responseCode = "200", description = "Following users retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No users followed")
    public DefaultResponse<List<CreateUserResponseDto>> getFollowing(
            @RequestParam UUID userId) {

        List<CreateUserResponseDto> following = connectionService.getFollowing(userId);
        if (following.isEmpty()) {
            return new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "No users followed", null);
        }
        return new DefaultResponse<>(following);
    }

    // Unfollow a user
    @DeleteMapping("/unfollow")
    @Operation(summary = "Unfollow a user")
    @ApiResponse(responseCode = "200", description = "User unfollowed successfully")
    @ApiResponse(responseCode = "400", description = "Unable to unfollow user")
    public DefaultResponse<String> unfollowUser( @RequestParam UUID followerId, @RequestParam UUID followingId) {

        connectionService.unfollowUser(followerId, followingId);
        return new DefaultResponse<>(HttpStatus.OK.value(), "User unfollowed successfully", null);
    }

    // Check if a user follows another user
    @GetMapping("/isFollowing")
    @Operation(summary = "Check if a user is following another user")
    @ApiResponse(responseCode = "200", description = "Following status retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public DefaultResponse<Boolean> isFollowing(@RequestParam UUID followerId, @RequestParam UUID followingId) {
        boolean follows = connectionService.isFollowing(followerId, followingId);
        return new DefaultResponse<>(follows);
    }

    @GetMapping("/followers")
    @Operation(summary = "Get all followers of a user")
    @ApiResponse(responseCode = "200", description = "Followers retrieved successfully")
    @ApiResponse(responseCode = "404", description = "No followers found")
    public DefaultResponse<List<CreateUserResponseDto>> getAllFollowers(@RequestParam UUID userId) {

        List<CreateUserResponseDto> followers = connectionService.getAllFollowers(userId);
        if (followers.isEmpty()) {
            return new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "No followers found", null);
        }
        return new DefaultResponse<>(followers);
    }



}
