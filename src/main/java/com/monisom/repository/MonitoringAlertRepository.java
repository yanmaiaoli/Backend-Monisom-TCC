package com.monisom.repository;

import com.monisom.entity.MonitoringAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonitoringAlertRepository extends JpaRepository<MonitoringAlert, UUID> {

    List<MonitoringAlert> findByMonitoringIdOrderByWindowEndAsc(UUID monitoringId);

    Optional<MonitoringAlert> findTop1ByMonitoringIdOrderByWindowEndDesc(UUID monitoringId);

    @Modifying
    @Query(value = """
            INSERT INTO monitoring_alert (monitoring_id, type, severity, value, message, window_end)
            VALUES (:monitoringId, :type, :severity, :value, :message, :windowEnd)
            ON CONFLICT (monitoring_id, type, window_end) DO NOTHING
            """, nativeQuery = true)
    int upsert(@Param("monitoringId") UUID monitoringId,
               @Param("type") String type,
               @Param("severity") String severity,
               @Param("value") double value,
               @Param("message") String message,
               @Param("windowEnd") OffsetDateTime windowEnd);
}
