package com.monisom.dto;

import java.time.Instant;
import java.util.UUID;

public record LiveMonitoringResponse(
        UUID monitoringId,
        String roomName,
        String className,
        Instant startedAt,
        String level,
        Double currentLeq,
        AlertResponse lastAlert) {
}
