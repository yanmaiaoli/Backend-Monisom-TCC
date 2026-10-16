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

import java.util.UUID;

@Entity
@Table(name = "monitoring_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringSummary {

    @Id
    private UUID id;

    @Column(name = "monitoring_id", nullable = false, unique = true)
    private UUID monitoringId;

    @Column(name = "leq_total", nullable = false)
    private Double leqTotal;

    @Column(nullable = false)
    private Double lmin;

    @Column(nullable = false)
    private Double lmax;

    @Column(name = "count", nullable = false)
    private Long count;

    @Column(nullable = false)
    private String level;

    @Column
    private Double l10;

    @Column
    private Double l50;

    @Column
    private Double l90;
}
