package com.monisom.service;

import com.monisom.repository.MonitoringRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MonitoringFinalizationJob {

    private static final Logger log = LoggerFactory.getLogger(MonitoringFinalizationJob.class);

    private final MonitoringRepository monitoringRepository;
    private final MonitoringResultService resultService;
    private final String orphanInterval;

    public MonitoringFinalizationJob(
            MonitoringRepository monitoringRepository,
            MonitoringResultService resultService,
            @Value("${app.orphan-minutes:3}") int orphanMinutes) {
        this.monitoringRepository = monitoringRepository;
        this.resultService = resultService;
        this.orphanInterval = orphanMinutes + " minutes";
    }

    @Scheduled(fixedDelay = 30_000)
    public void finalizeFinishedMonitorings() {
        markOrphanMonitorings();

        List<UUID> ids = monitoringRepository.findFinishedWithoutSummary();
        for (UUID id : ids) {
            try {
                resultService.finalize(id);
                log.info("Monitoramento finalizado: {}", id);
            } catch (Exception e) {
                log.error("Erro ao finalizar monitoramento {}", id, e);
            }
        }
    }

    private void markOrphanMonitorings() {
        try {
            resultService.markOrphanMonitorings(orphanInterval);
        } catch (Exception e) {
            log.error("Erro ao marcar monitoramentos órfãos", e);
        }
    }
}
