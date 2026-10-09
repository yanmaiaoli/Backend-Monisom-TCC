package com.monisom.controller;

import com.monisom.dto.TeacherRequest;
import com.monisom.dto.TeacherResponse;
import com.monisom.service.CurrentAdminService;
import com.monisom.service.TeacherService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;
    private final CurrentAdminService currentAdminService;

    public TeacherController(TeacherService teacherService, CurrentAdminService currentAdminService) {
        this.teacherService = teacherService;
        this.currentAdminService = currentAdminService;
    }

    @GetMapping
    public List<TeacherResponse> list() {
        return teacherService.list(currentAdminService.currentSchoolId());
    }

    @PostMapping
    public TeacherResponse create(@Valid @RequestBody TeacherRequest request) {
        return teacherService.create(currentAdminService.currentSchoolId(), request);
    }
}
