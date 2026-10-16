package com.monisom.dto;

import java.time.Instant;

public record WindowPoint(
        Instant windowStart,
        Instant windowEnd,
        Double leq,
        Double lmin,
        Double lmax,
        Long count,
        Double l10,
        Double l50,
        Double l90) {
}
