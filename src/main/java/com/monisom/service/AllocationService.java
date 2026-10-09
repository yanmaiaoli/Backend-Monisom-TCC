package com.monisom.service;

import com.monisom.dto.TeacherResponse;
import com.monisom.entity.ClassTeacher;
import com.monisom.entity.SchoolTeacherId;
import com.monisom.entity.Teacher;
import com.monisom.repository.ClassGroupRepository;
import com.monisom.repository.ClassTeacherRepository;
import com.monisom.repository.SchoolTeacherRepository;
import com.monisom.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AllocationService {

    private final ClassTeacherRepository classTeacherRepository;
    private final ClassGroupRepository classGroupRepository;
    private final TeacherRepository teacherRepository;
    private final SchoolTeacherRepository schoolTeacherRepository;

    public AllocationService(ClassTeacherRepository classTeacherRepository,
                             ClassGroupRepository classGroupRepository,
                             TeacherRepository teacherRepository,
                             SchoolTeacherRepository schoolTeacherRepository) {
        this.classTeacherRepository = classTeacherRepository;
        this.classGroupRepository = classGroupRepository;
        this.teacherRepository = teacherRepository;
        this.schoolTeacherRepository = schoolTeacherRepository;
    }

    public List<TeacherResponse> listTeachers(UUID schoolId, UUID classId) {
        verifyClass(schoolId, classId);
        return teacherRepository.findByClassId(classId).stream()
                .map(t -> new TeacherResponse(t.getId(), t.getName(), t.getEmail()))
                .toList();
    }

    @Transactional
    public TeacherResponse allocate(UUID schoolId, UUID classId, UUID teacherId) {
        verifyClass(schoolId, classId);
        boolean inSchool = schoolTeacherRepository.existsById(new SchoolTeacherId(schoolId, teacherId));
        if (!inSchool) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não pertence a esta escola");
        }
        if (classTeacherRepository.existsByClassIdAndTeacherId(classId, teacherId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Professor já lotado nesta turma");
        }
        classTeacherRepository.save(ClassTeacher.builder()
                .classId(classId)
                .teacherId(teacherId)
                .build());
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor não encontrado"));
        return new TeacherResponse(teacher.getId(), teacher.getName(), teacher.getEmail());
    }

    private void verifyClass(UUID schoolId, UUID classId) {
        classGroupRepository.findById(classId)
                .filter(c -> c.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));
    }
}
