package com.letsgo.user_service.user_service.service;

import com.letsgo.user_service.user_service.Repository.RoleRepository;
import com.letsgo.user_service.user_service.Repository.UserRepository;
import com.letsgo.user_service.user_service.dto.CreateUserRequestDto;
import com.letsgo.user_service.user_service.dto.CreateUserResponseDto;
import com.letsgo.user_service.user_service.model.Role;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.letsgo.user_service.user_service.mapper.UserMapper.mapToResponseDTO;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(UserService.class);


    private Role getDefaultRole() {
        // Try to find the default role (e.g., ROLE_USER)
        return roleRepository.findByName(RoleEnum.USER)
                .orElseGet(() -> {
                    // If the role doesn't exist, create it
                    Role defaultRole = new Role();
                    defaultRole.setName(RoleEnum.USER);
                    defaultRole.setDescription("Default role for regular users");
                    return roleRepository.save(defaultRole);
                });
    }



    // Create user with hashed password
    public CreateUserResponseDto createUser(CreateUserRequestDto requestDto) {
        logger.info("requestDto received: {}", requestDto);
        String email = requestDto.email();
        logger.info("requestDto user by email: {}", requestDto.email());

        Optional<User> existingUser = userRepository.findByEmail(email);
        logger.info("found user by email: {}", requestDto.email());

        if (existingUser.isPresent()) {
            throw new DuplicateKeyException("User with the email address already exists");
        }

        String hashedPassword = passwordEncoder.encode(requestDto.password());
        User newUser = new User();
        newUser.setEmail(requestDto.email());         // Ensure this is correct
        newUser.setFirstName(requestDto.firstName()); // Ensure this is correct
        newUser.setLastName(requestDto.lastName());   // Ensure this is correct
        newUser.setPassword(hashedPassword);

        // Assign roles
        Set<Role> roles = requestDto.roles() == null || requestDto.roles().isEmpty()
                ? Set.of(getDefaultRole()) // Assign default role if no roles are provided
                : requestDto.roles().stream()
                .map(roleEnum -> roleService.getRoleByName(roleEnum))  // Assuming you have a method to convert each role
                .collect(Collectors.toSet());

        newUser.setRoles(roles);

        // Save the user to the repository
        User savedUser = userRepository.save(newUser);

        // Return the response DTO after mapping
        return mapToResponseDTO(savedUser);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public CreateUserResponseDto updatedUser(UUID id, CreateUserRequestDto createUserRequestDto) {
        Optional<User> existingUser = userRepository.findById(id);

        String hashedPassword = passwordEncoder.encode(createUserRequestDto.password());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setEmail(createUserRequestDto.email());
            user.setFirstName(createUserRequestDto.firstName());
            user.setLastName(createUserRequestDto.lastName());

//            user.setBio(createUserRequestDto.());
            user.setPassword(hashedPassword);
            user.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            // Return the response DTO after mapping
            return mapToResponseDTO(savedUser);
        }
        throw new RuntimeException("User not found");
    }

    public Set<RoleEnum> getRoles(UUID id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return existingUser.getRoles().stream()
                .map(Role::getName) // Assuming Role has a getName() method that returns RoleEnum
                .collect(Collectors.toSet());
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
