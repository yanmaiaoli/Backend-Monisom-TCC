package com.monisom.repository;

import com.monisom.entity.MonitoringWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringWindowRepository extends JpaRepository<MonitoringWindow, UUID> {

    List<MonitoringWindow> findByMonitoringIdOrderByWindowStartAsc(UUID monitoringId);

    Optional<MonitoringWindow> findTop1ByMonitoringIdOrderByWindowStartDesc(UUID monitoringId);

    @Modifying
    @Query(value = """
            INSERT INTO monitoring_window (monitoring_id, window_start, window_end, leq, lmin, lmax, count, l10, l50, l90)
            VALUES (:monitoringId, :windowStart, :windowEnd, :leq, :lmin, :lmax, :count, :l10, :l50, :l90)
            ON CONFLICT (monitoring_id, window_start) DO UPDATE SET
              window_end = EXCLUDED.window_end,
              leq = EXCLUDED.leq,
              lmin = EXCLUDED.lmin,
              lmax = EXCLUDED.lmax,
              count = EXCLUDED.count,
              l10 = EXCLUDED.l10,
              l50 = EXCLUDED.l50,
              l90 = EXCLUDED.l90
            """, nativeQuery = true)
    int upsert(@Param("monitoringId") UUID monitoringId,
               @Param("windowStart") OffsetDateTime windowStart,
               @Param("windowEnd") OffsetDateTime windowEnd,
               @Param("leq") double leq,
               @Param("lmin") double lmin,
               @Param("lmax") double lmax,
               @Param("count") long count,
               @Param("l10") double l10,
               @Param("l50") double l50,
               @Param("l90") double l90);
}
