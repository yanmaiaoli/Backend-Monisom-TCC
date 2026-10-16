package com.monisom.repository;

import com.monisom.entity.MonitoringReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface MonitoringReadingRepository extends JpaRepository<MonitoringReading, UUID> {

    List<MonitoringReading> findTop1000BySeqGreaterThanOrderBySeqAsc(Long seq);

    @Modifying
    @Query("DELETE FROM MonitoringReading r WHERE r.recordedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") OffsetDateTime cutoff);

    @Modifying
    @Query(value = """
            INSERT INTO monitoring_window (monitoring_id, window_start, window_end, leq, lmin, lmax, count, l10, l50, l90)
            SELECT monitoring_id,
                   to_timestamp(floor(extract(epoch FROM recorded_at) / 120) * 120),
                   to_timestamp(floor(extract(epoch FROM recorded_at) / 120) * 120 + 120),
                   10 * log10(avg(power(10, laeq / 10))),
                   min(laeq),
                   max(laeq),
                   count(*),
                   percentile_cont(0.90) WITHIN GROUP (ORDER BY laeq),
                   percentile_cont(0.50) WITHIN GROUP (ORDER BY laeq),
                   percentile_cont(0.10) WITHIN GROUP (ORDER BY laeq)
            FROM monitoring_reading
            WHERE monitoring_id = :monitoringId
            GROUP BY monitoring_id, floor(extract(epoch FROM recorded_at) / 120)
            ON CONFLICT (monitoring_id, window_start) DO NOTHING
            """, nativeQuery = true)
    int backfillWindows(@Param("monitoringId") UUID monitoringId);

    @Query(value = """
            SELECT 10 * log10(avg(power(10, laeq / 10))), min(laeq), max(laeq), count(*),
                   percentile_cont(0.90) WITHIN GROUP (ORDER BY laeq),
                   percentile_cont(0.50) WITHIN GROUP (ORDER BY laeq),
                   percentile_cont(0.10) WITHIN GROUP (ORDER BY laeq)
            FROM monitoring_reading
            WHERE monitoring_id = :monitoringId
            """, nativeQuery = true)
    List<Object[]> computeTotal(@Param("monitoringId") UUID monitoringId);
}
