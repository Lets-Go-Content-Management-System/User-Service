package com.letsgo.user_service.user_service.service;

import com.letsgo.user_service.user_service.Repository.UserRepository;
import com.letsgo.user_service.user_service.model.User;
import exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// custom spring security service impl
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws NotFoundException {
        Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

        logger.info("Loading user by email: {}", email);

        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            logger.error("User not found with email: {}", email);
            throw new NotFoundException(String.format("User does not exist, email: %s", email));
        }

        User user = userOptional.get();
        logger.info("User found: {}", user.getEmail());

        // Convert user's role to GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // Assuming role.getName() returns RoleEnum name
                .collect(Collectors.toList());

        logger.info("Authorities mapped: {}", authorities);

        // Return Spring Security's UserDetails object
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }

}
