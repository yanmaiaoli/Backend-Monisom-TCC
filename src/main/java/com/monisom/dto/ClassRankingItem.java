package com.monisom.dto;

public record ClassRankingItem(
        String className,
        String turno,
        Integer capacidade,
        double leqTotal,
        String level,
        long monitorings) {
}
