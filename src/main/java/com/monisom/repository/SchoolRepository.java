package com.monisom.repository;

import com.monisom.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SchoolRepository extends JpaRepository<School, UUID> {
}
