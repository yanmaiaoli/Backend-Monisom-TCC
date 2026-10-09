package com.monisom.repository;

import com.monisom.entity.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, UUID> {

    boolean existsByClassIdAndTeacherId(UUID classId, UUID teacherId);
}
