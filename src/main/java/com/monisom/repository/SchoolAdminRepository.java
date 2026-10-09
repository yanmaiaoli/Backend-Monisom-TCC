package com.monisom.repository;

import com.monisom.entity.SchoolAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SchoolAdminRepository extends JpaRepository<SchoolAdmin, UUID> {

    Optional<SchoolAdmin> findByEmail(String email);
}
