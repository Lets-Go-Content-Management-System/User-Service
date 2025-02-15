package com.letsgo.user_service.user_service.config;

import com.letsgo.user_service.user_service.Repository.RoleRepository;
import com.letsgo.user_service.user_service.Repository.UserRepository;
import com.letsgo.user_service.user_service.model.Role;
import com.letsgo.user_service.user_service.model.User;
import com.letsgo.user_service.user_service.model.enums.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create superadmin role if it doesn't exist
        if (userRepository.findByEmail("superadmin@letsgo.com").isEmpty()) {
            Role superAdminRole = roleRepository.findByName(RoleEnum.SUPER_ADMIN)
                    .orElseGet(() -> roleRepository.save( new Role(RoleEnum.SUPER_ADMIN, "Super Administrator Role")));
            User superAdmin = new User();
            superAdmin.setFirstName("Super");
            superAdmin.setLastName("Admin");
            superAdmin.setEmail("superadmin@letsgo.com");
            superAdmin.setPassword(passwordEncoder.encode("superadmin"));
            superAdmin.getRoles().add(superAdminRole);
            userRepository.save(superAdmin);
        }

        // Create default user role if it doesn't exist
        Role userRole = roleRepository.findByName(RoleEnum.USER)
                .orElseGet(() -> roleRepository.save(new Role(RoleEnum.USER, "Default User Role")));
    }

}
