package com.monisom.service;

import com.monisom.dto.TeacherRequest;
import com.monisom.dto.TeacherResponse;
import com.monisom.entity.SchoolTeacher;
import com.monisom.entity.SchoolTeacherId;
import com.monisom.entity.Teacher;
import com.monisom.repository.SchoolTeacherRepository;
import com.monisom.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final SchoolTeacherRepository schoolTeacherRepository;
    private final SupabaseAuthService supabaseAuthService;

    public TeacherService(TeacherRepository teacherRepository,
                          SchoolTeacherRepository schoolTeacherRepository,
                          SupabaseAuthService supabaseAuthService) {
        this.teacherRepository = teacherRepository;
        this.schoolTeacherRepository = schoolTeacherRepository;
        this.supabaseAuthService = supabaseAuthService;
    }

    public List<TeacherResponse> list(UUID schoolId) {
        return teacherRepository.findBySchoolId(schoolId).stream()
                .map(t -> new TeacherResponse(t.getId(), t.getName(), t.getEmail()))
                .toList();
    }

    @Transactional
    public TeacherResponse create(UUID schoolId, TeacherRequest request) {
        try {
            UUID userId = supabaseAuthService.createUser(request.email(), request.password());
            teacherRepository.save(Teacher.builder()
                    .id(userId)
                    .name(request.name())
                    .email(request.email())
                    .build());
            schoolTeacherRepository.save(SchoolTeacher.builder()
                    .id(new SchoolTeacherId(schoolId, userId))
                    .build());
            return new TeacherResponse(userId, request.name(), request.email());
        } catch (RestClientResponseException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }
    }
}
