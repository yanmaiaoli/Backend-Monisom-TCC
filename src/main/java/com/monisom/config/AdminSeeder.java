package com.monisom.config;

import com.monisom.entity.SchoolAdmin;
import com.monisom.repository.SchoolAdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final SchoolAdminRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(SchoolAdminRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        repository.save(SchoolAdmin.builder()
                .email("admin1@monisom.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .name("Admin Escola 1")
                .schoolId(UUID.fromString("a0000000-0000-4000-8000-000000000001"))
                .build());
        repository.save(SchoolAdmin.builder()
                .email("admin2@monisom.com")
                .passwordHash(passwordEncoder.encode("admin123"))
                .name("Admin Escola 2")
                .schoolId(UUID.fromString("a0000000-0000-4000-8000-000000000002"))
                .build());
    }
}
