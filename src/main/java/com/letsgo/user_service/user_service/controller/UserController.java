package com.letsgo.user_service.user_service.controller;


import com.letsgo.user_service.user_service.Helper.JwtHelper;
import com.letsgo.user_service.user_service.Repository.UserRepository;
import com.letsgo.user_service.user_service.controller.responses.DefaultResponse;
import com.letsgo.user_service.user_service.dto.LoginDto;
import com.letsgo.user_service.user_service.dto.CreateUserRequestDto;
import com.letsgo.user_service.user_service.dto.CreateUserResponseDto;
import com.letsgo.user_service.user_service.mapper.UserMapper;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import com.letsgo.user_service.user_service.service.RoleService;
import com.letsgo.user_service.user_service.service.TokenBlackListService;
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
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")

public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenBlackListService tokenBlackListService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;


    @PostMapping
    @Operation(summary = "Create a new user and return token", description = "Create a new user in the system with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DefaultResponse<CreateUserResponseDto>> createUser(@RequestBody CreateUserRequestDto userCreateDTO) {
        try {
            // Create the user
            CreateUserResponseDto createdUser = userService.createUser(userCreateDTO);

            // Fetch the newly created user
            User user = userService.findUserByEmail(createdUser.email())
                    .orElseThrow(() -> new RuntimeException("User not found after creation"));


            Set<RoleEnum> roleEnums = user.getRoles().stream()
                    .map(role -> role.getName()) // Assuming getName() returns RoleEnum
                    .collect(Collectors.toSet());
            // Generate JWT Token
            String token = JwtHelper.generateToken(
                    user.getEmail(),
                    user.getLastName(),
                    user.getLastName(),
                    user.getId(),
                    roleEnums // Assuming user.getRole() returns RoleEnum
            );


            // Create a new UserResponseDTO with the token
            CreateUserResponseDto userResponseWithToken = new CreateUserResponseDto(
                    createdUser.id(),
                    createdUser.email(),
                    createdUser.firstName(),
                    createdUser.lastName(),
                    createdUser.roles(),
                    token
            );

            // Return the response
            return ResponseEntity.ok(new DefaultResponse<>(200, "User created successfully", userResponseWithToken));

        } catch (RuntimeException e) {
            // Handle exceptions (e.g., invalid input, database errors)
            String error = "User already exists" + userCreateDTO.email();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new DefaultResponse<>(400, error, null));
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DefaultResponse<>(500, "Internal server error", null));
        }
    }


    @PostMapping(value = "/login")
    @Operation(summary = "Authenticate user and return token")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<DefaultResponse<CreateUserResponseDto>> login(@RequestBody LoginDto loginDto) {
        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password())
            );

            // Fetch user details from database
            User user = userService.findUserByEmail(loginDto.email())
                    .orElseThrow(() -> new NotFoundException("User not found with email: " + loginDto.email()));

            Set<RoleEnum> roleEnums = user.getRoles().stream()
                    .map(role -> role.getName()) // Assuming getName() returns RoleEnum
                    .collect(Collectors.toSet());

            // Generate JWT Token
            String token = JwtHelper.generateToken(
                    user.getEmail(),
                    user.getLastName(),
                    user.getLastName(),
                    user.getId(),
                    roleEnums // Assuming user.getRole() returns RoleEnum
            );



            // Create a new UserResponseDTO with the token
            CreateUserResponseDto userResponseWithToken = new CreateUserResponseDto(
                    user.getId(),
                    user.getEmail(),
                    user.getLastName(),
                    user.getFirstName(),
                    roleEnums,
                    token
            );


            return ResponseEntity.ok(new DefaultResponse<>(200, "Login successful", userResponseWithToken));

        } catch (BadCredentialsException e) {
            DefaultResponse<CreateUserResponseDto> response = new DefaultResponse<>(401, "Invalid credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DefaultResponse<>(500, "Internal server error", null));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the user by blacklisting the token.")
    @ApiResponse(responseCode = "200", description = "User logged out successfully")
    @ApiResponse(responseCode = "400", description = "Logout failed")
    public ResponseEntity<DefaultResponse<Void>> logout(@RequestBody String token) {
        // Invalidate the token by adding it to the blacklist
        boolean success = tokenBlackListService.invalidateToken(token);

        // Return a response based on whether the token was successfully blacklisted
        if (success) {
            return ResponseEntity.ok(new DefaultResponse<>(200, "User logged out successfully", null));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new DefaultResponse<>(400, "Logout failed", null));
    }




    @GetMapping("/{id}")
    @Operation(summary = "Retrieve a user by ID", description = "Fetches a user by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<DefaultResponse<CreateUserResponseDto>> getUserById (@PathVariable UUID id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            CreateUserResponseDto userResponseDTO = UserMapper.mapToResponseDTO(userOpt.get()); // Map to DTO
            DefaultResponse<CreateUserResponseDto> response = new DefaultResponse<>(userResponseDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            DefaultResponse<CreateUserResponseDto> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping
    @Operation(summary = "Retrieve all users", description = "Fetches a list of all users in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users found"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<DefaultResponse<List<CreateUserResponseDto>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            DefaultResponse<List<CreateUserResponseDto>> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "No users found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        // Map users to UserResponseDTOs
        List<CreateUserResponseDto> userResponseDTOs = users.stream()
                .map(user -> userMapper.mapToResponseDTO(user))  // Map each User to UserResponseDTO
                .collect(Collectors.toList());  // Collect the mapped DTOs into a list

        DefaultResponse<List<CreateUserResponseDto>> response = new DefaultResponse<>(userResponseDTOs);
        return new ResponseEntity<>(response, HttpStatus.OK);
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
            return ResponseEntity.ok(new DefaultResponse<>(200, "User deleted successfully", null));
        } catch (Exception e) {
            DefaultResponse<Void> response = new DefaultResponse<>(HttpStatus.NOT_FOUND.value(), "User not found", null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }



    @Operation(summary = "Update user details", description = "Updates the user information and generates a new token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "An error occurred")
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<DefaultResponse<CreateUserResponseDto>> updateUser(
            @PathVariable UUID userId,
            @RequestBody CreateUserRequestDto createUserRequestDto) {

        try {
            // Call service to update the user and get the response DTO
            CreateUserResponseDto updatedUserResponse = userService.updatedUser(userId, createUserRequestDto);


            // get updatedUser
            Optional<User> user = userRepository.findById(updatedUserResponse.id());

            if (user.isPresent()) {
                // Regenerate the JWT Token with updated user details
                Set<RoleEnum> roleEnums = user.get().getRoles().stream()
                        .map(role -> role.getName()) // Assuming getName() returns RoleEnum
                        .collect(Collectors.toSet());

                String token = JwtHelper.generateToken(
                        user.get().getEmail(),
                        user.get().getLastName(),
                        user.get().getFirstName(),
                        user.get().getId(),
                        roleEnums // Updated roles
                );
                // Create a response DTO with the updated token
                CreateUserResponseDto userResponseWithToken = new CreateUserResponseDto(
                        updatedUserResponse.id(),
                        updatedUserResponse.email(),
                        updatedUserResponse.firstName(),
                        updatedUserResponse.lastName(),
                        updatedUserResponse.roles(),
                        token
                );

                // Return the success response with the updated user information and token
                return ResponseEntity.ok(new DefaultResponse<>(200, "User updated successfully", userResponseWithToken));

            } else {
                return ResponseEntity.status(404).body(new DefaultResponse<>(404, "User not found", null));
            }


        } catch (RuntimeException e) {
            // If user not found or other runtime exceptions occur
            return ResponseEntity.status(404).body(new DefaultResponse<>(404, "User not found", null));
        } catch (Exception e) {
            // Catch other possible exceptions
            return ResponseEntity.status(500).body(new DefaultResponse<>(500, "An error occurred", null));
        }

    }


}


