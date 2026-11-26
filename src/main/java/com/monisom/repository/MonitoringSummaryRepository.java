package com.monisom.repository;

import com.monisom.entity.MonitoringSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
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

    @Query(value = """
            SELECT count(s.monitoring_id),
                   avg(s.leq_total),
                   count(*) FILTER (WHERE s.level IN ('ATTENTION','CRITICAL'))
            FROM monitoring m
            JOIN class c ON c.id = m.class_id
            JOIN monitoring_summary s ON s.monitoring_id = m.id
            WHERE c.school_id = :schoolId
              AND m.finished_at IS NOT NULL
              AND m.started_at >= :from
              AND m.started_at < :to
              AND (:turno = '' OR c.turno::text = :turno)
            """, nativeQuery = true)
    List<Object[]> indicatorsStats(@Param("schoolId") UUID schoolId,
                                   @Param("from") OffsetDateTime from,
                                   @Param("to") OffsetDateTime to,
                                   @Param("turno") String turno);

    @Query(value = """
            SELECT c.name,
                   c.turno::text,
                   c.capacidade,
                   avg(s.leq_total),
                   count(m.id)
            FROM monitoring m
            JOIN class c ON c.id = m.class_id
            JOIN monitoring_summary s ON s.monitoring_id = m.id
            WHERE c.school_id = :schoolId
              AND m.finished_at IS NOT NULL
              AND m.started_at >= :from
              AND m.started_at < :to
              AND (:turno = '' OR c.turno::text = :turno)
            GROUP BY c.id, c.name, c.turno, c.capacidade
            ORDER BY avg(s.leq_total) DESC
            """, nativeQuery = true)
    List<Object[]> rankClasses(@Param("schoolId") UUID schoolId,
                               @Param("from") OffsetDateTime from,
                               @Param("to") OffsetDateTime to,
                               @Param("turno") String turno);
}
