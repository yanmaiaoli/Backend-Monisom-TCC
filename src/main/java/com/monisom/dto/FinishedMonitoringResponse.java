package com.monisom.dto;

import java.time.Instant;
import java.util.UUID;

public record FinishedMonitoringResponse(
        UUID monitoringId,
        String roomName,
        String className,
        Instant startedAt,
        Instant finishedAt) {
}
