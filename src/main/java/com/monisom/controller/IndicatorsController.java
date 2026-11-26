package com.monisom.controller;

import com.monisom.dto.IndicatorsResponse;
import com.monisom.service.CurrentAdminService;
import com.monisom.service.IndicatorsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/indicators")
public class IndicatorsController {

    private final IndicatorsService service;
    private final CurrentAdminService currentAdminService;

    public IndicatorsController(IndicatorsService service, CurrentAdminService currentAdminService) {
        this.service = service;
        this.currentAdminService = currentAdminService;
    }

    @GetMapping
    public IndicatorsResponse indicators(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String turno) {
        return service.indicators(currentAdminService.currentSchoolId(), from, to, turno);
    }
}
