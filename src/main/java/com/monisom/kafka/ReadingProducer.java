package com.monisom.kafka;

import com.monisom.entity.MonitoringReading;
import com.monisom.repository.MonitoringReadingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReadingProducer {

    private static final Logger log = LoggerFactory.getLogger(ReadingProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final MonitoringReadingRepository repository;
    private final String topic;

    private long lastSeq = 0L;

    public ReadingProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            MonitoringReadingRepository repository,
            @Value("${app.kafka.topic.reading:monitoring-reading}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.repository = repository;
        this.topic = topic;
    }

    @Scheduled(fixedDelayString = "${app.kafka.producer.poll-ms:5000}")
    public void poll() {
        try {
            List<MonitoringReading> readings =
                    repository.findTop1000BySeqGreaterThanOrderBySeqAsc(lastSeq);
            if (readings.isEmpty()) {
                return;
            }
            for (MonitoringReading r : readings) {
                kafkaTemplate.send(topic, r.getMonitoringId().toString(), toJson(r));
                lastSeq = r.getSeq();
            }
            log.info("Produtor: {} leitura(s) enviadas para o tópico '{}' (lastSeq={})",
                    readings.size(), topic, lastSeq);
        } catch (Exception e) {
            log.error("Erro no produtor de leituras: {}", e.getMessage(), e);
        }
    }

    private String toJson(MonitoringReading r) {
        return "{\"monitoring_id\":\"" + r.getMonitoringId()
                + "\",\"recorded_at\":\"" + r.getRecordedAt()
                + "\",\"laeq\":" + r.getLaeq() + "}";
    }
}
