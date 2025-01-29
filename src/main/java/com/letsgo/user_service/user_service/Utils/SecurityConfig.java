//package com.letsgo.user_service.user_service.Utils;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())  // Optionally disable CSRF protection if needed
//                .authorizeRequests(auth -> auth
//                        .anyRequest().permitAll()  // Allow all requests without authentication
//                );
//
//        return http.build();
//    }
//}
