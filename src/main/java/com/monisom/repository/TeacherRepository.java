package com.monisom.repository;

import com.monisom.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TeacherRepository extends JpaRepository<Teacher, UUID> {

    @Query(value = "SELECT t.id, t.name, t.email FROM teacher t " +
            "JOIN school_teacher st ON st.teacher_id = t.id " +
            "WHERE st.school_id = :schoolId ORDER BY t.name", nativeQuery = true)
    List<Teacher> findBySchoolId(@Param("schoolId") UUID schoolId);

    @Query(value = "SELECT t.id, t.name, t.email FROM teacher t " +
            "JOIN class_teacher ct ON ct.teacher_id = t.id " +
            "WHERE ct.class_id = :classId ORDER BY t.name", nativeQuery = true)
    List<Teacher> findByClassId(@Param("classId") UUID classId);
}
