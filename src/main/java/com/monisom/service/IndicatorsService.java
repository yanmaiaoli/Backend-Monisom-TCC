package com.monisom.service;

import com.monisom.dto.ClassRankingItem;
import com.monisom.dto.IndicatorsResponse;
import com.monisom.dto.IndicatorsSummary;
import com.monisom.kafka.model.LeqLevel;
import com.monisom.repository.MonitoringAlertRepository;
import com.monisom.repository.MonitoringSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class IndicatorsService {

    private final MonitoringSummaryRepository summaryRepository;
    private final MonitoringAlertRepository alertRepository;

    public IndicatorsService(MonitoringSummaryRepository summaryRepository,
                             MonitoringAlertRepository alertRepository) {
        this.summaryRepository = summaryRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public IndicatorsResponse indicators(UUID schoolId, LocalDate from, LocalDate to, String turno) {
        OffsetDateTime fromDt = from != null
                ? from.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
                : OffsetDateTime.of(1900, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime toDt = to != null
                ? to.plusDays(1).atStartOfDay(ZoneOffset.UTC).toOffsetDateTime()
                : OffsetDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        String turnoFiltro = turno == null ? "" : turno;

        Object[] stats = summaryRepository.indicatorsStats(schoolId, fromDt, toDt, turnoFiltro).get(0);
        long total = ((Number) stats[0]).longValue();
        Double averageLeq = stats[1] == null ? null : ((Number) stats[1]).doubleValue();
        long critical = ((Number) stats[2]).longValue();
        long alerts = ((Number) alertRepository.countIndicators(schoolId, fromDt, toDt, turnoFiltro).get(0)[0]).longValue();

        IndicatorsSummary summary = new IndicatorsSummary(total, averageLeq, alerts, critical);

        List<ClassRankingItem> ranking = summaryRepository.rankClasses(schoolId, fromDt, toDt, turnoFiltro).stream()
                .map(IndicatorsService::toRankingItem)
                .toList();

        return new IndicatorsResponse(summary, ranking);
    }

    private static ClassRankingItem toRankingItem(Object[] row) {
        String className = (String) row[0];
        String turno = (String) row[1];
        Integer capacidade = row[2] == null ? null : ((Number) row[2]).intValue();
        double leqTotal = ((Number) row[3]).doubleValue();
        long monitorings = ((Number) row[4]).longValue();
        String level = LeqLevel.from(leqTotal).name();
        return new ClassRankingItem(className, turno, capacidade, leqTotal, level, monitorings);
    }
}
