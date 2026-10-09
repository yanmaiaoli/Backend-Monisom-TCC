package com.monisom.controller;

import com.monisom.dto.AllocationRequest;
import com.monisom.dto.TeacherResponse;
import com.monisom.service.AllocationService;
import com.monisom.service.CurrentAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classes/{classId}/teachers")
public class AllocationController {

    private final AllocationService allocationService;
    private final CurrentAdminService currentAdminService;

    public AllocationController(AllocationService allocationService, CurrentAdminService currentAdminService) {
        this.allocationService = allocationService;
        this.currentAdminService = currentAdminService;
    }

    @GetMapping
    public List<TeacherResponse> list(@PathVariable UUID classId) {
        return allocationService.listTeachers(currentAdminService.currentSchoolId(), classId);
    }

    @PostMapping
    public TeacherResponse allocate(@PathVariable UUID classId, @Valid @RequestBody AllocationRequest request) {
        return allocationService.allocate(currentAdminService.currentSchoolId(), classId, request.teacherId());
    }
}
