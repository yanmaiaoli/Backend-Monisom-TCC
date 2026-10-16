package com.monisom.kafka.model;

import java.time.Instant;

public record LeqStatus(
        String monitoringId,
        LeqLevel level,
        double leq,
        Instant timestamp) {
}
