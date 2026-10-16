package com.monisom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "monitoring_window")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringWindow {

    @Id
    private UUID id;

    @Column(name = "monitoring_id", nullable = false)
    private UUID monitoringId;

    @Column(name = "window_start", nullable = false)
    private OffsetDateTime windowStart;

    @Column(name = "window_end", nullable = false)
    private OffsetDateTime windowEnd;

    @Column(nullable = false)
    private Double leq;

    @Column(nullable = false)
    private Double lmin;

    @Column(nullable = false)
    private Double lmax;

    @Column(name = "count", nullable = false)
    private Long count;

    @Column
    private Double l10;

    @Column
    private Double l50;

    @Column
    private Double l90;
}
