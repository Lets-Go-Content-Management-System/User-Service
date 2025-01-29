package com.letsgo.user_service.user_service.controller;


import com.letsgo.user_service.user_service.Helper.JwtHelper;
import com.letsgo.user_service.user_service.controller.responses.DefaultResponse;
import com.letsgo.user_service.user_service.dto.LoginDto;
import com.letsgo.user_service.user_service.dto.LoginResponse;
import com.letsgo.user_service.user_service.dto.UserCreateDTO;
import com.letsgo.user_service.user_service.dto.UserResponseDTO;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import com.letsgo.user_service.user_service.service.UserService;
import exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;


    @PostMapping
    @Operation(summary = "Create a new user" , description = "Create a new user in the system with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Invalid input data")
    })
    public ResponseEntity<DefaultResponse<UserResponseDTO>> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        UserResponseDTO createdUser = userService.createUser(userCreateDTO);
        DefaultResponse<UserResponseDTO> response = new DefaultResponse<>(HttpStatus.CREATED.value(), "Success", createdUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login")
    @Operation(summary = "Authenticate user and return token")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    public ResponseEntity<DefaultResponse<LoginResponse>> login(@RequestBody LoginDto loginDto) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
            );

            // Fetch user details from database
            User user = userService.findUserByEmail(loginDto.email())
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + loginDto.email()));

            // Generate JWT Token
            String token = JwtHelper.generateToken(
                    user.getEmail(),
                    user.getFullName(),
                    user.getId(),
                    user.getRole() // Assuming user.getRole() returns RoleEnum
            );

            // Create response
            LoginResponse loginResponse = new LoginResponse(user.getEmail(), token);
            return ResponseEntity.ok(new DefaultResponse<>(200, "Login successful", loginResponse));

        } catch (BadCredentialsException e) {
            // Fix: return instead of throw
            DefaultResponse<LoginResponse> response = new DefaultResponse<>(401, "Invalid credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }


    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a user by ID", description = "Fetches a user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DefaultResponse<UserResponseDTO>> getUserById (@PathVariable UUID id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            UserResponseDTO userResponseDTO = userService.mapToResponseDTO(userOpt.get()); // Map to DTO
            DefaultResponse<UserResponseDTO> response = new DefaultResponse<>(userResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            DefaultResponse<UserResponseDTO> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping
    @Operation(summary = "Retrieve all users", description = "Fetches a list of all users in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<DefaultResponse<List<UserResponseDTO>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            DefaultResponse<List<UserResponseDTO>> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "No users found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Map users to UserResponseDTOs
        List<UserResponseDTO> userResponseDTOs = users.stream()
                .map(user -> userService.mapToResponseDTO(user))  // Map each User to UserResponseDTO
                .collect(Collectors.toList());  // Collect the mapped DTOs into a list

        DefaultResponse<List<UserResponseDTO>> response = new DefaultResponse<>(userResponseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //get Role Of Connected User
    @GetMapping("/role/{id}")
    @Operation(summary = "Get role of a user", description = "Gets role of a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DefaultResponse<RoleEnum>> getRoleOfUser(@PathVariable UUID id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            RoleEnum role = userService.getRole(id);
            DefaultResponse<RoleEnum> response = new DefaultResponse<>(role);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            DefaultResponse<RoleEnum> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }



    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes an existing user from the system based on the provided ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DefaultResponse<Void>> deleteUser(@PathVariable UUID id) {
        try {
            userService.deleteUser(id);
            DefaultResponse<Void> response = new DefaultResponse<>(HttpStatus.NO_CONTENT.value(), "User deleted successfully", null);
            return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            DefaultResponse<Void> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

}
