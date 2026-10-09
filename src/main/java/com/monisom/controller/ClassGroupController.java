package com.monisom.controller;

import com.monisom.dto.ClassGroupRequest;
import com.monisom.dto.ClassGroupResponse;
import com.monisom.service.ClassGroupService;
import com.monisom.service.CurrentAdminService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/classes")
public class ClassGroupController {

    private final ClassGroupService service;
    private final CurrentAdminService currentAdminService;

    public ClassGroupController(ClassGroupService service, CurrentAdminService currentAdminService) {
        this.service = service;
        this.currentAdminService = currentAdminService;
    }

    @GetMapping
    public List<ClassGroupResponse> list() {
        return service.list(currentAdminService.currentSchoolId());
    }

    @PostMapping
    public ClassGroupResponse create(@Valid @RequestBody ClassGroupRequest request) {
        return service.create(currentAdminService.currentSchoolId(), request);
    }

    @PutMapping("/{id}")
    public ClassGroupResponse update(@PathVariable UUID id, @Valid @RequestBody ClassGroupRequest request) {
        return service.update(currentAdminService.currentSchoolId(), id, request);
    }
}
