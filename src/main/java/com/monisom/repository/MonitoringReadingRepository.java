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
}
