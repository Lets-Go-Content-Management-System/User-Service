package com.letsgo.user_service.user_service.service;

import com.letsgo.user_service.user_service.Repository.UserRepository;
import com.letsgo.user_service.user_service.dto.UserCreateDTO;
import com.letsgo.user_service.user_service.dto.UserResponseDTO;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    // mapper for responseDto and User
    public UserResponseDTO mapToResponseDTO(User user) {
      return new UserResponseDTO(
              user.getId(),
              user.getFullName(),
              user.getEmail(),
              user.getRole(),
              user.getCreatedAt(),
              user.getUpdatedAt()
      );
    }

    // Create user with hashed password
    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
      String email = userCreateDTO.email();
      Optional<User> existingUser = userRepository.findByEmail(email);
      if(existingUser.isPresent()) {
          throw new DuplicateKeyException("User with the email address already exists");
      }
      String hashedPassword = passwordEncoder.encode(userCreateDTO.password());
      User newUser = new User();
        newUser.setFullName(userCreateDTO.fullName());
        newUser.setEmail(userCreateDTO.email());
        newUser.setPassword(hashedPassword);

        // Save the user to the repository
        User savedUser = userRepository.save(newUser);

        // Return the response DTO after mapping
        return mapToResponseDTO(savedUser);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UserResponseDTO updatedUser(UUID id, User updatedUser) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setFullName(updatedUser.getFullName());
            user.setEmail(updatedUser.getEmail());
            user.setPassword(updatedUser.getPassword());
            user.setRole(updatedUser.getRole());
            user.setUpdatedAt(updatedUser.getUpdatedAt());
            User savedUser = userRepository.save(user);
            // Return the response DTO after mapping
            return mapToResponseDTO(savedUser);
        }
        throw new RuntimeException("User not found");
    }

    public RoleEnum getRole (UUID id) {
        Optional<User> existingUser = userRepository.findById(id);
        if(existingUser.isPresent()) {
            return existingUser.get().getRole();
        }
        throw new RuntimeException("User not found");
    }

    // Delete a user by ID
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    // Retrieve a user by ID
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    // Retrieve all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
