package com.hospital;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import entity.User;
import repository.UserRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            boolean adminExists = userRepository.findAll()
                    .stream()
                    .anyMatch(user ->
                            "ADMIN".equalsIgnoreCase(user.getRole())
                    );

            if (!adminExists) {

                User admin = new User();

                admin.setUsername("admin");

                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );

                admin.setRole("ADMIN");

                admin.setEnabled(true);

                userRepository.save(admin);

                System.out.println(
                        "=========================================="
                );

                System.out.println(
                        "DEFAULT ADMIN CREATED"
                );

                System.out.println(
                        "Username: admin"
                );

                System.out.println(
                        "Password: Admin@123"
                );

                System.out.println(
                        "=========================================="
                );

            } else {

                System.out.println(
                        "ADMIN USER ALREADY EXISTS"
                );
            }
        };
    }
}