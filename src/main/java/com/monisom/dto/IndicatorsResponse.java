package com.monisom.dto;

import java.util.List;

public record IndicatorsResponse(
        IndicatorsSummary summary,
        List<ClassRankingItem> ranking) {
}
