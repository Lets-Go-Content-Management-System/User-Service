//package com.letsgo.user_service.user_service.config;
//
//
//import com.letsgo.user_service.user_service.Repository.RoleRepository;
//import com.letsgo.user_service.user_service.Repository.UserRepository;
//import com.letsgo.user_service.user_service.model.Role;
//import com.letsgo.user_service.user_service.model.User;
//import com.letsgo.user_service.user_service.model.enums.RoleEnum;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class DataLoader implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Create roles
//        Role userRole = new Role(RoleEnum.USER);
//        Role adminRole = new Role(RoleEnum.ADMIN);
//        roleRepository.save(userRole);
//        roleRepository.save(adminRole);
//
//        // Create users
////        User user = new User();
////        user.setFullName("John Doe");
////        user.setEmail("user@example.com");
////        user.setPassword(passwordEncoder.encode("password"));
////        user.setRoles(Set.of(userRole));
////        userRepository.save(user);
//
//
//
//        Set<Role> roles = new HashSet<>();
//        roles.add(new Role(RoleEnum.USER));
//
//        User admin = new User();
//        admin.setFullName("Admin User");
//        admin.setEmail("admin@example.com");
//        admin.setPassword(passwordEncoder.encode("password"));
//        admin.setRoles(roles);
//        userRepository.save(admin);
//    }
//}