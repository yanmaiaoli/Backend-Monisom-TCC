package com.monisom.dto;

import java.time.Instant;

public record AlertResponse(
        String type,
        String severity,
        Double value,
        String message,
        Instant timestamp) {
}
