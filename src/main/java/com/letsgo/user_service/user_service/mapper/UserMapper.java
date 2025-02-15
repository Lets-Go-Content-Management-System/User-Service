package com.letsgo.user_service.user_service.mapper;

import com.letsgo.user_service.user_service.dto.CreateUserResponseDto;
import com.letsgo.user_service.user_service.model.Role;
import com.letsgo.user_service.user_service.model.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class UserMapper {

    public static CreateUserResponseDto mapToResponseDTO(User user, String token) {
        return new CreateUserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRoles().stream()
                        .map(role -> role.getName()) // Assuming role.getName() returns RoleEnum
                        .collect(Collectors.toSet()), // Map roles to RoleEnum
                token


        );
    }

    public static CreateUserResponseDto mapToResponseDTO(User user) {
        return mapToResponseDTO(user, null); // Overload without token
    }
}
