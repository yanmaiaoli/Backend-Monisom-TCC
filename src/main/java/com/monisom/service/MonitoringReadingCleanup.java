package com.monisom.service;

import com.monisom.repository.MonitoringReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;

@Component
public class MonitoringReadingCleanup {

    private static final Logger log = LoggerFactory.getLogger(MonitoringReadingCleanup.class);

    private final MonitoringReadingRepository repository;
    private final TransactionTemplate transactionTemplate;
    private final long retentionDays;

    public MonitoringReadingCleanup(
            MonitoringReadingRepository repository,
            PlatformTransactionManager transactionManager,
            @Value("${app.retention.days:20}") long retentionDays) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.retentionDays = retentionDays;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 6 * 60 * 60 * 1000L)
    public void cleanup() {
        OffsetDateTime cutoff = OffsetDateTime.now().minusDays(retentionDays);
        Integer deleted = transactionTemplate.execute(status -> repository.deleteOlderThan(cutoff));
        log.info("Limpeza de monitoring_reading concluída: {} leitura(s) anteriores a {} removida(s) (retenção de {} dias)",
                deleted == null ? 0 : deleted, cutoff, retentionDays);
    }
}
