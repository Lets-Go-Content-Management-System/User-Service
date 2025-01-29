package com.letsgo.user_service.user_service.dto;

import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


public record UserResponseDTO( UUID id,
                               String fullName,
                               String email,
                               RoleEnum role,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
