package com.letsgo.user_service.user_service.dto;

import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record UserCreateDTO(
        @NotBlank(message = "Name cannot be blank")
        String fullName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
        String password,

        RoleEnum role){

}
