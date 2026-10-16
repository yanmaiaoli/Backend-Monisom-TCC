package com.monisom.kafka.model;

import java.time.Instant;

public record Alert(
        String monitoringId,
        AlertType type,
        Severity severity,
        double value,
        String message,
        Instant timestamp) {
}
