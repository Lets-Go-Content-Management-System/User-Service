package com.letsgo.user_service.user_service.controller;

import com.letsgo.user_service.user_service.Helper.JwtHelper;
import com.letsgo.user_service.user_service.controller.responses.DefaultResponse;
import com.letsgo.user_service.user_service.dto.CreateUserResponseDto;
import com.letsgo.user_service.user_service.model.Role;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import com.letsgo.user_service.user_service.service.RoleService;
import com.letsgo.user_service.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roles")public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Get all roles", description = "Returns a list of all roles in the system")
    @ApiResponse(responseCode = "200", description = "Successfully fetched the list of roles")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        try {
            List<Role> roles = roleService.getAllRoles();
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Get role by name", description = "Returns a role by name in the system")
    @ApiResponse(responseCode = "200", description = "Successfully fetched the role")
    @ApiResponse(responseCode = "404", description = "Role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @GetMapping("/{roleName}")
    public ResponseEntity<Role> getRoleByName(@PathVariable RoleEnum roleName) {
        try {
            Role role = roleService.getRoleByName(roleName);
            return ResponseEntity.ok(role);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Create a new role", description = "Create a new role")
    @ApiResponse(responseCode = "201", description = "Role successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping
    public ResponseEntity<Role> createRole(
            @RequestParam RoleEnum roleName,
            @RequestParam String description) {
        try {
            Role role = roleService.createRole(roleName, description);
            return ResponseEntity.status(201).body(role);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(description = "Assign a role to a user")
    @ApiResponse(responseCode = "200", description = "Role successfully assigned to the user")
    @ApiResponse(responseCode = "404", description = "User or role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/assign")

    public ResponseEntity<DefaultResponse<CreateUserResponseDto>> assignRoleToUser(
            @RequestParam UUID userId,
            @RequestParam RoleEnum roleName) {
        try {
            // Assign role to the user
            roleService.assignRoleToUser(userId, roleName);

            // Fetch the user after role assignment
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                User existingUser = user.get();

                // Get the user's roles after role assignment
                Set<RoleEnum> roleEnums = existingUser.getRoles().stream()
                        .map(role -> role.getName()) // Assuming getName() returns RoleEnum
                        .collect(Collectors.toSet());

                // Generate a new JWT token with updated roles
                String token = JwtHelper.generateToken(
                        existingUser.getEmail(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        existingUser.getId(),
                        roleEnums // Updated roles
                );

                // Create response DTO with the token
                CreateUserResponseDto userResponseWithToken = new CreateUserResponseDto(
                        existingUser.getId(),
                        existingUser.getEmail(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        roleEnums,
                        token
                );

                // Return the success response
                return ResponseEntity.ok(new DefaultResponse<>(200, "User role upgraded successfully", userResponseWithToken));

            } else {
                // User not found
                return ResponseEntity.status(404).body(new DefaultResponse<>(404, "User not found", null));
            }

        } catch (EntityNotFoundException e) {
            // Handle entity not found exception
            return ResponseEntity.status(404).body(new DefaultResponse<>(404, "User or role not found", null));
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(500).body(new DefaultResponse<>(500, "An error occurred", null));
        }
    }


    @Operation(description = "Remove a role from a user")
    @ApiResponse(responseCode = "200", description = "Role successfully removed from the user")
    @ApiResponse(responseCode = "404", description = "User or role not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @PostMapping("/remove")
    public ResponseEntity<DefaultResponse<CreateUserResponseDto>> removeRoleFromUser(
            @RequestParam UUID userId,
            @RequestParam RoleEnum roleName) {
        try {
            // Remove role from the user
            roleService.removeRoleFromUser(userId, roleName);

            // Fetch the user after role removal
            Optional<User> user = userService.getUserById(userId);
            if (user.isPresent()) {
                User existingUser = user.get();

                // Get the user's updated roles after role removal
                Set<RoleEnum> roleEnums = existingUser.getRoles().stream()
                        .map(role -> role.getName()) // Assuming getName() returns RoleEnum
                        .collect(Collectors.toSet());

                // Generate a new JWT token with the updated roles
                String token = JwtHelper.generateToken(
                        existingUser.getEmail(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        existingUser.getId(),
                        roleEnums // Updated roles
                );

                // Create a response DTO with the token and user details
                CreateUserResponseDto userResponseWithToken = new CreateUserResponseDto(
                        existingUser.getId(),
                        existingUser.getEmail(),
                        existingUser.getFirstName(),
                        existingUser.getLastName(),
                        roleEnums,
                        token
                );

                // Return the success response with the updated user information and token
                return ResponseEntity.ok(new DefaultResponse<>(200, "User role removed successfully", userResponseWithToken));
            } else {
                // User not found
                return ResponseEntity.status(404).body(new DefaultResponse<>(404, "User not found", null));
            }
        } catch (EntityNotFoundException e) {
            // Handle the case when the entity or role is not found
            return ResponseEntity.status(404).body(new DefaultResponse<>(404, "User or role not found", null));
        } catch (Exception e) {
            // Handle other exceptions
            return ResponseEntity.status(500).body(new DefaultResponse<>(500, "An error occurred", null));
        }
    }

}



