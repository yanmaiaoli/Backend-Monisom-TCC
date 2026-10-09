package com.monisom.service;

import com.monisom.dto.ClassGroupRequest;
import com.monisom.dto.ClassGroupResponse;
import com.monisom.entity.ClassGroup;
import com.monisom.repository.ClassGroupRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ClassGroupService {

    private final ClassGroupRepository repository;

    public ClassGroupService(ClassGroupRepository repository) {
        this.repository = repository;
    }

    public List<ClassGroupResponse> list(UUID schoolId) {
        return repository.findBySchoolIdOrderByNameAsc(schoolId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ClassGroupResponse create(UUID schoolId, ClassGroupRequest request) {
        ClassGroup saved = repository.save(ClassGroup.builder()
                .schoolId(schoolId)
                .name(request.name())
                .turno(request.turno())
                .capacidade(request.capacidade())
                .build());
        return toResponse(saved);
    }

    @Transactional
    public ClassGroupResponse update(UUID schoolId, UUID id, ClassGroupRequest request) {
        ClassGroup entity = repository.findById(id)
                .filter(c -> c.getSchoolId().equals(schoolId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada"));
        entity.setName(request.name());
        entity.setTurno(request.turno());
        entity.setCapacidade(request.capacidade());
        return toResponse(entity);
    }

    private ClassGroupResponse toResponse(ClassGroup c) {
        return new ClassGroupResponse(c.getId(), c.getName(), c.getTurno(), c.getCapacidade());
    }
}
