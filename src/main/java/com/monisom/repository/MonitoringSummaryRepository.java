package com.monisom.repository;

import com.monisom.entity.MonitoringSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface MonitoringSummaryRepository extends JpaRepository<MonitoringSummary, UUID> {

    Optional<MonitoringSummary> findByMonitoringId(UUID monitoringId);

    @Modifying
    @Query(value = """
            INSERT INTO monitoring_summary (monitoring_id, leq_total, lmin, lmax, count, level, l10, l50, l90)
            VALUES (:monitoringId, :leqTotal, :lmin, :lmax, :count, :level, :l10, :l50, :l90)
            ON CONFLICT (monitoring_id) DO UPDATE SET
              leq_total = EXCLUDED.leq_total,
              lmin = EXCLUDED.lmin,
              lmax = EXCLUDED.lmax,
              count = EXCLUDED.count,
              level = EXCLUDED.level,
              l10 = EXCLUDED.l10,
              l50 = EXCLUDED.l50,
              l90 = EXCLUDED.l90
            """, nativeQuery = true)
    int upsert(@Param("monitoringId") UUID monitoringId,
               @Param("leqTotal") double leqTotal,
               @Param("lmin") double lmin,
               @Param("lmax") double lmax,
               @Param("count") long count,
               @Param("level") String level,
               @Param("l10") double l10,
               @Param("l50") double l50,
               @Param("l90") double l90);
}
