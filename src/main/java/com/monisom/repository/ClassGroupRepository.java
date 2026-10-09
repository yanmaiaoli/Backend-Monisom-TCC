package com.monisom.repository;

import com.monisom.entity.ClassGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassGroupRepository extends JpaRepository<ClassGroup, UUID> {

    List<ClassGroup> findBySchoolIdOrderByNameAsc(UUID schoolId);
}
