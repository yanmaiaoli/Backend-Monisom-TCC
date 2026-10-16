package com.monisom.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MonitoringDetailResponse(
        UUID monitoringId,
        String roomName,
        String className,
        String teacherName,
        Instant startedAt,
        Instant finishedAt,
        Integer perception,
        Integer stress,
        MonitoringSummaryResponse summary,
        List<WindowPoint> windows,
        List<AlertResponse> alerts) {
}
