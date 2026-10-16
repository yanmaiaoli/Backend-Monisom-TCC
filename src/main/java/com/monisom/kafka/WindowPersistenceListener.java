package com.monisom.kafka;

import com.monisom.kafka.model.WindowedReading;
import com.monisom.repository.MonitoringWindowRepository;
import com.monisom.service.MonitoringSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class WindowPersistenceListener {

    private static final Logger log = LoggerFactory.getLogger(WindowPersistenceListener.class);

    private final MonitoringWindowRepository repository;
    private final MonitoringSnapshot snapshot;

    public WindowPersistenceListener(MonitoringWindowRepository repository, MonitoringSnapshot snapshot) {
        this.repository = repository;
        this.snapshot = snapshot;
    }

    @KafkaListener(topics = "monitoring-window-aggregate",
            containerFactory = "windowReadingKafkaListenerContainerFactory")
    @Transactional
    public void onWindow(WindowedReading reading) {
        repository.upsert(
                UUID.fromString(reading.monitoringId()),
                reading.windowStart().atOffset(ZoneOffset.UTC),
                reading.windowEnd().atOffset(ZoneOffset.UTC),
                reading.leq(),
                reading.lmin(),
                reading.lmax(),
                reading.count(),
                reading.l10(),
                reading.l50(),
                reading.l90());
        snapshot.updateWindow(reading);
        log.info("Janela persistida: monitoring={} inicio={} leq={} (count={})",
                reading.monitoringId(), reading.windowStart(), reading.leq(), reading.count());
    }
}
