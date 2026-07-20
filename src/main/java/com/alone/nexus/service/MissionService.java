package com.alone.nexus.service;

import com.alone.nexus.dto.MissionCreateRequest;
import com.alone.nexus.dto.MissionReportRequest;
import com.alone.nexus.dto.MissionResponse;
import com.alone.nexus.exception.MissionNotFoundException;
import com.alone.nexus.model.Mission;
import com.alone.nexus.repository.MissionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MissionService {

    private static final Logger log = LoggerFactory.getLogger(MissionService.class);

    private final MissionRepository missionRepository;
    private final AgentService agentService;
    private final ObjectMapper objectMapper;

    public MissionService(MissionRepository missionRepository, AgentService agentService,
                           ObjectMapper objectMapper) {
        this.missionRepository = missionRepository;
        this.agentService = agentService;
        this.objectMapper = objectMapper;
    }

    public MissionResponse createMission(MissionCreateRequest request) {
        agentService.getAgentByName(request.getAgentName());

        Mission mission = new Mission(request.getAgentName(), request.getAction(), writeJson(request.getParams()));
        mission = missionRepository.save(mission);

        log.info("Mision creada para {}: {}", mission.getAgentName(), mission.getAction());
        return toResponse(mission);
    }

    @Transactional
    public List<MissionResponse> getPendingMissions(String agentName) {
        List<Mission> pending = missionRepository.findPendingMissionsWithLock(agentName);

        Instant now = Instant.now();
        for (Mission mission : pending) {
            mission.setStatus(Mission.MissionStatus.IN_PROGRESS);
            mission.setAssignedAt(now);
        }
        missionRepository.saveAll(pending);

        return pending.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MissionResponse reportMissionResult(UUID missionId, MissionReportRequest request) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new MissionNotFoundException("Mision no encontrada: " + missionId));

        Mission.MissionStatus finalStatus = request.getStatus() == MissionReportRequest.Status.COMPLETED
                ? Mission.MissionStatus.COMPLETED
                : Mission.MissionStatus.FAILED;

        mission.setStatus(finalStatus);
        mission.setResult(writeJson(request.getResult()));
        mission.setCompletedAt(Instant.now());
        mission = missionRepository.save(mission);

        log.info("Mision {} reportada como {}", missionId, finalStatus);
        return toResponse(mission);
    }

    private MissionResponse toResponse(Mission mission) {
        return new MissionResponse(
                mission.getId(),
                mission.getAgentName(),
                mission.getAction(),
                mission.getStatus().name(),
                readJsonMap(mission.getParams()),
                readJsonMap(mission.getResult()),
                mission.getCreatedAt(),
                mission.getAssignedAt(),
                mission.getCompletedAt()
        );
    }

    private String writeJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el payload a JSON", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readJsonMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
