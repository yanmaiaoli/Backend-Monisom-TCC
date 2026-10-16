package com.monisom.kafka.processor;

import com.monisom.kafka.KafkaSerdes;
import com.monisom.kafka.model.Alert;
import com.monisom.kafka.model.AlertType;
import com.monisom.kafka.model.LeqLevel;
import com.monisom.kafka.model.LeqStatus;
import com.monisom.kafka.model.Severity;
import com.monisom.kafka.model.WindowedReading;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.ArrayList;
import java.util.List;

public final class AlertStateProcessor {

    public static final String SOURCE_TOPIC = "monitoring-window-aggregate";
    public static final String STATE_TOPIC = "monitoring-state";
    public static final String ALERTS_TOPIC = "alerts";

    private static final double COMFORT_LIMIT = 35.0;
    private static final double ADEQUATE_LIMIT = 65.0;
    private static final double CRITICAL_LIMIT = 85.0;
    private static final double PEAK_THRESHOLD = 90.0;

    private AlertStateProcessor() {
    }

    public static Topology buildTopology() {
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, WindowedReading> source = builder.stream(
                SOURCE_TOPIC,
                Consumed.with(Serdes.String(), KafkaSerdes.WINDOWED_READING)
        );

        source
                .mapValues(AlertStateProcessor::toStatus)
                .to(STATE_TOPIC, Produced.with(Serdes.String(), KafkaSerdes.LEQ_STATUS));

        source
                .flatMapValues(AlertStateProcessor::evaluateAlerts)
                .to(ALERTS_TOPIC, Produced.with(Serdes.String(), KafkaSerdes.ALERT));

        return builder.build();
    }

    private static LeqStatus toStatus(WindowedReading r) {
        return new LeqStatus(
                r.monitoringId(),
                levelOf(r.leq()),
                r.leq(),
                r.windowEnd()
        );
    }

    private static LeqLevel levelOf(double leq) {
        if (leq <= COMFORT_LIMIT) {
            return LeqLevel.COMFORTABLE;
        }
        if (leq <= ADEQUATE_LIMIT) {
            return LeqLevel.ADEQUATE;
        }
        if (leq <= CRITICAL_LIMIT) {
            return LeqLevel.ATTENTION;
        }
        return LeqLevel.CRITICAL;
    }

    private static List<Alert> evaluateAlerts(WindowedReading r) {
        List<Alert> alerts = new ArrayList<>();
        LeqLevel level = levelOf(r.leq());

        if (level == LeqLevel.ATTENTION || level == LeqLevel.CRITICAL) {
            Severity severity = level == LeqLevel.CRITICAL ? Severity.CRITICAL : Severity.HIGH;
            alerts.add(new Alert(
                    r.monitoringId(),
                    AlertType.HIGH_LEVEL,
                    severity,
                    r.leq(),
                    String.format("Nível médio de %.1f dB na janela", r.leq()),
                    r.windowEnd()
            ));
        }

        if (r.lmax() > PEAK_THRESHOLD) {
            alerts.add(new Alert(
                    r.monitoringId(),
                    AlertType.HIGH_PEAK,
                    Severity.HIGH,
                    r.lmax(),
                    String.format("Pico de %.1f dB na janela", r.lmax()),
                    r.windowEnd()
            ));
        }

        return alerts;
    }
}
