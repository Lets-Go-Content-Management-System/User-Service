package com.letsgo.user_service.user_service.dto;

import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;


public record CreateUserResponse(
        UUID id,
        @Schema(description = "User name")
        String fullName,
        @Schema(description = "User email")
        String email,
        @Schema(description = "User role")
        RoleEnum role,
        @Schema(description = "JWT token")
        String token
) {}
