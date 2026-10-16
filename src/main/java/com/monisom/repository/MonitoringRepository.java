package com.monisom.repository;

import com.monisom.entity.Monitoring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MonitoringRepository extends JpaRepository<Monitoring, UUID> {

    List<Monitoring> findByClassGroup_SchoolIdAndFinishedAtIsNullOrderByStartedAtDesc(UUID schoolId);

    List<Monitoring> findByClassGroup_SchoolIdAndFinishedAtIsNotNullOrderByFinishedAtDesc(UUID schoolId);

    @Query(value = """
            SELECT m.id FROM monitoring m
            WHERE m.finished_at IS NOT NULL
              AND NOT EXISTS (SELECT 1 FROM monitoring_summary s WHERE s.monitoring_id = m.id)
            """, nativeQuery = true)
    List<UUID> findFinishedWithoutSummary();

    @Modifying
    @Query(value = """
            UPDATE monitoring m
            SET finished_at = sub.last_read
            FROM (
                SELECT monitoring_id, max(recorded_at) AS last_read
                FROM monitoring_reading
                GROUP BY monitoring_id
            ) sub
            WHERE m.id = sub.monitoring_id
              AND m.finished_at IS NULL
              AND sub.last_read < now() - CAST(:interval AS interval)
            """, nativeQuery = true)
    int markStaleByLastReading(@Param("interval") String interval);

    @Modifying
    @Query(value = """
            UPDATE monitoring m
            SET finished_at = m.started_at
            WHERE m.finished_at IS NULL
              AND m.started_at < now() - CAST(:interval AS interval)
              AND NOT EXISTS (SELECT 1 FROM monitoring_reading r WHERE r.monitoring_id = m.id)
            """, nativeQuery = true)
    int markStaleWithNoReadings(@Param("interval") String interval);
}
