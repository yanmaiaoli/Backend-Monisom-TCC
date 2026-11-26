package com.monisom.dto;

public record IndicatorsSummary(
        long totalMonitorings,
        Double averageLeq,
        long totalAlerts,
        long criticalMonitorings) {
}
