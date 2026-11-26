package com.monisom.kafka;

import com.monisom.kafka.model.Alert;
import com.monisom.repository.MonitoringAlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class AlertPersistenceListener {

    private static final Logger log = LoggerFactory.getLogger(AlertPersistenceListener.class);

    private final MonitoringAlertRepository repository;

    public AlertPersistenceListener(MonitoringAlertRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = "alerts", containerFactory = "alertKafkaListenerContainerFactory")
    @Transactional
    public void onAlert(Alert alert) {
        repository.upsert(
                UUID.fromString(alert.monitoringId()),
                alert.type().name(),
                alert.severity().name(),
                alert.value(),
                alert.message(),
                alert.timestamp().atOffset(ZoneOffset.UTC));
        log.info("Alerta persistido: monitoring={} tipo={} severidade={}",
                alert.monitoringId(), alert.type(), alert.severity());
    }
}
