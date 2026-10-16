package com.monisom.service;

import com.monisom.dto.AlertResponse;
import com.monisom.dto.FinishedMonitoringResponse;
import com.monisom.dto.LiveMonitoringResponse;
import com.monisom.dto.MonitoringDetailResponse;
import com.monisom.dto.MonitoringSummaryResponse;
import com.monisom.dto.WindowPoint;
import com.monisom.entity.Monitoring;
import com.monisom.entity.MonitoringAlert;
import com.monisom.entity.MonitoringSummary;
import com.monisom.entity.MonitoringWindow;
import com.monisom.entity.Room;
import com.monisom.entity.Teacher;
import com.monisom.kafka.model.LeqLevel;
import com.monisom.repository.MonitoringAlertRepository;
import com.monisom.repository.MonitoringRepository;
import com.monisom.repository.MonitoringSummaryRepository;
import com.monisom.repository.MonitoringWindowRepository;
import com.monisom.repository.RoomRepository;
import com.monisom.repository.TeacherRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class MonitoringService {

    private final MonitoringRepository monitoringRepository;
    private final MonitoringWindowRepository windowRepository;
    private final MonitoringSummaryRepository summaryRepository;
    private final MonitoringAlertRepository alertRepository;
    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;
    private final MonitoringResultService resultService;

    public MonitoringService(MonitoringRepository monitoringRepository,
                             MonitoringWindowRepository windowRepository,
                             MonitoringSummaryRepository summaryRepository,
                             MonitoringAlertRepository alertRepository,
                             RoomRepository roomRepository,
                             TeacherRepository teacherRepository,
                             MonitoringResultService resultService) {
        this.monitoringRepository = monitoringRepository;
        this.windowRepository = windowRepository;
        this.summaryRepository = summaryRepository;
        this.alertRepository = alertRepository;
        this.roomRepository = roomRepository;
        this.teacherRepository = teacherRepository;
        this.resultService = resultService;
    }

    @Transactional(readOnly = true)
    public List<LiveMonitoringResponse> live(UUID schoolId) {
        return monitoringRepository.findByClassGroup_SchoolIdAndFinishedAtIsNullOrderByStartedAtDesc(schoolId)
                .stream()
                .map(this::toLive)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FinishedMonitoringResponse> finished(UUID schoolId) {
        return monitoringRepository.findByClassGroup_SchoolIdAndFinishedAtIsNotNullOrderByFinishedAtDesc(schoolId)
                .stream()
                .map(this::toFinished)
                .toList();
    }

    @Transactional
    public MonitoringDetailResponse detail(UUID id, UUID schoolId) {
        Monitoring monitoring = monitoringRepository.findById(id)
                .filter(m -> m.getClassGroup().getSchoolId().equals(schoolId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoramento não encontrado"));

        if (monitoring.getFinishedAt() != null && summaryRepository.findByMonitoringId(id).isEmpty()) {
            resultService.finalize(id);
        }

        return toDetail(monitoring);
    }

    private LiveMonitoringResponse toLive(Monitoring m) {
        MonitoringWindow latest = windowRepository.findTop1ByMonitoringIdOrderByWindowStartDesc(m.getId())
                .orElse(null);
        MonitoringAlert lastAlert = alertRepository.findTop1ByMonitoringIdOrderByWindowEndDesc(m.getId())
                .orElse(null);

        String level = latest != null ? LeqLevel.from(latest.getLeq()).name() : null;
        Double currentLeq = latest != null ? latest.getLeq() : null;
        AlertResponse alert = lastAlert != null ? toAlert(lastAlert) : null;

        return new LiveMonitoringResponse(
                m.getId(),
                roomName(m.getRoomId()),
                m.getClassGroup().getName(),
                m.getStartedAt().toInstant(),
                level,
                currentLeq,
                alert);
    }

    private FinishedMonitoringResponse toFinished(Monitoring m) {
        return new FinishedMonitoringResponse(
                m.getId(),
                roomName(m.getRoomId()),
                m.getClassGroup().getName(),
                m.getStartedAt().toInstant(),
                m.getFinishedAt() != null ? m.getFinishedAt().toInstant() : null);
    }

    private MonitoringDetailResponse toDetail(Monitoring m) {
        MonitoringSummary summary = summaryRepository.findByMonitoringId(m.getId()).orElse(null);
        List<WindowPoint> windows = windowRepository.findByMonitoringIdOrderByWindowStartAsc(m.getId())
                .stream().map(this::toWindow).toList();
        List<AlertResponse> alerts = alertRepository.findByMonitoringIdOrderByWindowEndAsc(m.getId())
                .stream().map(this::toAlert).toList();

        return new MonitoringDetailResponse(
                m.getId(),
                roomName(m.getRoomId()),
                m.getClassGroup().getName(),
                teacherName(m.getTeacherId()),
                m.getStartedAt().toInstant(),
                m.getFinishedAt() != null ? m.getFinishedAt().toInstant() : null,
                m.getPerception(),
                m.getStress(),
                summary != null ? toSummary(summary) : null,
                windows,
                alerts);
    }

    private String roomName(UUID roomId) {
        if (roomId == null) {
            return null;
        }
        return roomRepository.findById(roomId).map(Room::getName).orElse(null);
    }

    private String teacherName(UUID teacherId) {
        if (teacherId == null) {
            return null;
        }
        return teacherRepository.findById(teacherId).map(Teacher::getName).orElse(null);
    }

    private MonitoringSummaryResponse toSummary(MonitoringSummary s) {
        return new MonitoringSummaryResponse(
                s.getLeqTotal(), s.getLmin(), s.getLmax(), s.getCount(), s.getLevel(),
                s.getL10(), s.getL50(), s.getL90());
    }

    private WindowPoint toWindow(MonitoringWindow w) {
        return new WindowPoint(
                w.getWindowStart().toInstant(),
                w.getWindowEnd().toInstant(),
                w.getLeq(),
                w.getLmin(),
                w.getLmax(),
                w.getCount(),
                w.getL10(),
                w.getL50(),
                w.getL90());
    }

    private AlertResponse toAlert(MonitoringAlert a) {
        return new AlertResponse(a.getType(), a.getSeverity(), a.getValue(), a.getMessage(), a.getWindowEnd().toInstant());
    }
}
