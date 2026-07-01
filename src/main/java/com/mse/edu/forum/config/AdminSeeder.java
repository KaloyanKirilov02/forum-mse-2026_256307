package com.mse.edu.forum.config;

import com.mse.edu.forum.domain.UserEntity;
import com.mse.edu.forum.domain.UserRole;
import com.mse.edu.forum.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("admin")) {
            return;
        }

        UserEntity admin = new UserEntity();
        admin.setUsername("admin");
        admin.setEmail("admin@forum.local");
        admin.setRole(UserRole.ADMIN);
        admin.setPasswordHash(passwordEncoder.encode("password"));

        userRepository.save(admin);
    }
}