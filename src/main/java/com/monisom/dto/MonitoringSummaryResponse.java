package com.monisom.dto;

public record MonitoringSummaryResponse(
        Double leqTotal,
        Double lmin,
        Double lmax,
        Long count,
        String level,
        Double l10,
        Double l50,
        Double l90) {
}
