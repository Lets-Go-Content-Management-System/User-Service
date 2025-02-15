package com.letsgo.user_service.user_service.dto;

import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;
import java.util.UUID;


public record CreateUserResponseDto(
        @Schema(description = "Unique identifier of the user")
        UUID id,
        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,
        @Schema(description = "First name of the user", example = "John")
        String firstName,
        @Schema(description = "Last name of the user", example = "Doe")
        String lastName,
        @Schema(description = "Roles assigned to the user", example = "[\"ROLE_USER\"]")
        Set<RoleEnum> roles,
        @Schema(description = "JWT token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {}
