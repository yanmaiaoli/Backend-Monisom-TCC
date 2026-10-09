package com.monisom.service;

import com.monisom.repository.SchoolAdminRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CurrentAdminService {

    private final SchoolAdminRepository repository;

    public CurrentAdminService(SchoolAdminRepository repository) {
        this.repository = repository;
    }

    public UUID currentSchoolId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin não encontrado"))
                .getSchoolId();
    }
}
