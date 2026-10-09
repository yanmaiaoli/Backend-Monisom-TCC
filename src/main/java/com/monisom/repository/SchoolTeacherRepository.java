package com.monisom.repository;

import com.monisom.entity.SchoolTeacher;
import com.monisom.entity.SchoolTeacherId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolTeacherRepository extends JpaRepository<SchoolTeacher, SchoolTeacherId> {
}
