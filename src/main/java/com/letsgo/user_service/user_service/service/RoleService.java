package com.letsgo.user_service.user_service.service;

import com.letsgo.user_service.user_service.Repository.RoleRepository;
import com.letsgo.user_service.user_service.Repository.UserRepository;
import com.letsgo.user_service.user_service.model.Role;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    public Role createRole(RoleEnum roleName, String description) {
        if (roleRepository.findByName(roleName).isPresent()) {
            logger.warn("Attempted to create an existing role: {}", roleName);
            throw new IllegalArgumentException("Role already exists.");
        }
        logger.info("Creating new role: {}", roleName);

        Role role = new Role();
        role.setName(roleName);
        role.setDescription(description);
        return roleRepository.save(role);
    }


    public Role getRoleFromString(RoleEnum roleName) {
        // Check if the role already exists in the database
        Optional<Role> existingRole = roleRepository.findByName(roleName);
        String description = "default role for " + roleName;

        // If it exists, return the existing role
        if (existingRole.isPresent()) {
            return existingRole.get();
        }

        // If not, create a new role or handle accordingly
        return createRole(roleName, description);
    }



    @Transactional
    public void assignRoleToUser(UUID userId, RoleEnum roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (!user.getRoles().add(role)) {
            user.getRoles().add(role);
        }
    }

    @Transactional
    public void removeRoleFromUser(UUID userId, RoleEnum roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));

        if (user.getRoles().add(role)) {
            user.getRoles().remove(role);

        }
    }


    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Page<Role> getAllRoles(Pageable pageable) {
        return roleRepository.findAll(pageable);
    }

    public Role getRoleByName(RoleEnum roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleName));
    }
}