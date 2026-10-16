package com.monisom.service;

import com.monisom.kafka.model.Alert;
import com.monisom.kafka.model.WindowedReading;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MonitoringSnapshot {

    private static final int MAX_ALERTS = 200;

    private final Map<String, WindowedReading> latestByMonitoring = new ConcurrentHashMap<>();
    private final Deque<Alert> recentAlerts = new ArrayDeque<>();

    public void updateWindow(WindowedReading reading) {
        latestByMonitoring.merge(reading.monitoringId(), reading, (old, cur) ->
                cur.windowStart().isAfter(old.windowStart()) ? cur : old);
    }

    public void addAlert(Alert alert) {
        synchronized (recentAlerts) {
            recentAlerts.addLast(alert);
            while (recentAlerts.size() > MAX_ALERTS) {
                recentAlerts.pollFirst();
            }
        }
    }

    public Optional<WindowedReading> latestWindow(String monitoringId) {
        return Optional.ofNullable(latestByMonitoring.get(monitoringId));
    }

    public List<Alert> recentAlerts() {
        synchronized (recentAlerts) {
            return List.copyOf(recentAlerts);
        }
    }
}
