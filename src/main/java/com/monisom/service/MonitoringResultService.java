package com.monisom.service;

import com.monisom.kafka.model.LeqLevel;
import com.monisom.repository.MonitoringReadingRepository;
import com.monisom.repository.MonitoringRepository;
import com.monisom.repository.MonitoringSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MonitoringResultService {

    private final MonitoringReadingRepository readingRepository;
    private final MonitoringRepository monitoringRepository;
    private final MonitoringSummaryRepository summaryRepository;

    public MonitoringResultService(MonitoringReadingRepository readingRepository,
                                   MonitoringRepository monitoringRepository,
                                   MonitoringSummaryRepository summaryRepository) {
        this.readingRepository = readingRepository;
        this.monitoringRepository = monitoringRepository;
        this.summaryRepository = summaryRepository;
    }

    @Transactional
    public void markOrphanMonitorings(String interval) {
        monitoringRepository.markStaleByLastReading(interval);
        monitoringRepository.markStaleWithNoReadings(interval);
    }

    @Transactional
    public void finalize(UUID monitoringId) {
        readingRepository.backfillWindows(monitoringId);

        List<Object[]> rows = readingRepository.computeTotal(monitoringId);
        if (rows.isEmpty()) {
            return;
        }
        Object[] row = rows.get(0);
        if (row[0] == null) {
            return;
        }

        double leqTotal = ((Number) row[0]).doubleValue();
        double lmin = ((Number) row[1]).doubleValue();
        double lmax = ((Number) row[2]).doubleValue();
        long count = ((Number) row[3]).longValue();
        double l10 = ((Number) row[4]).doubleValue();
        double l50 = ((Number) row[5]).doubleValue();
        double l90 = ((Number) row[6]).doubleValue();

        String level = LeqLevel.from(leqTotal).name();
        summaryRepository.upsert(monitoringId, leqTotal, lmin, lmax, count, level, l10, l50, l90);
    }
}
