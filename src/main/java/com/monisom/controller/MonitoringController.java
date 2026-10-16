package com.monisom.controller;

import com.monisom.dto.FinishedMonitoringResponse;
import com.monisom.dto.LiveMonitoringResponse;
import com.monisom.dto.MonitoringDetailResponse;
import com.monisom.service.CurrentAdminService;
import com.monisom.service.MonitoringService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final MonitoringService service;
    private final CurrentAdminService currentAdminService;

    public MonitoringController(MonitoringService service, CurrentAdminService currentAdminService) {
        this.service = service;
        this.currentAdminService = currentAdminService;
    }

    @GetMapping("/live")
    public List<LiveMonitoringResponse> live() {
        return service.live(currentAdminService.currentSchoolId());
    }

    @GetMapping("/finished")
    public List<FinishedMonitoringResponse> finished() {
        return service.finished(currentAdminService.currentSchoolId());
    }

    @GetMapping("/{id}")
    public MonitoringDetailResponse detail(@PathVariable UUID id) {
        return service.detail(id, currentAdminService.currentSchoolId());
    }
}
