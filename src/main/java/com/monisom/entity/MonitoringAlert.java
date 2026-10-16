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
@Table(name = "monitoring_alert")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonitoringAlert {

    @Id
    private UUID id;

    @Column(name = "monitoring_id", nullable = false)
    private UUID monitoringId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String severity;

    @Column(nullable = false)
    private Double value;

    @Column
    private String message;

    @Column(name = "window_end", nullable = false)
    private OffsetDateTime windowEnd;
}
