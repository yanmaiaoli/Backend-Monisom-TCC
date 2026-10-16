package com.monisom.kafka.model;

import java.time.Instant;

public record WindowedReading(
        String monitoringId,
        Instant windowStart,
        Instant windowEnd,
        double leq,
        double lmin,
        double lmax,
        long count,
        double l10,
        double l50,
        double l90) {
}
