package com.letsgo.user_service.user_service.dto;

import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserRequestDto(

        @Schema(description = "Email address of the user", example = "john.doe@example.com")
        String email,
        @Schema(description = "First name of the user", example = "John")
        String firstName,
        @Schema(description = "Last name of the user", example = "Doe")
        String lastName,
        @Schema(description = "Password for the user", example = "password123")
        String password,
        @Schema(description = "Roles to assign to the user", example = "[\"USER\"]")
        Set<RoleEnum> roles
){

}
