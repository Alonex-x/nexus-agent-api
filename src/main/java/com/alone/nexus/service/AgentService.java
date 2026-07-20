package com.alone.nexus.service;

import com.alone.nexus.dto.AgentRegisterRequest;
import com.alone.nexus.dto.AgentRegisterResponse;
import com.alone.nexus.dto.AgentStatusResponse;
import com.alone.nexus.exception.AgentNotFoundException;
import com.alone.nexus.model.Agent;
import com.alone.nexus.model.Mission;
import com.alone.nexus.repository.AgentRepository;
import com.alone.nexus.repository.MissionRepository;
import com.alone.nexus.util.ApiKeyGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final AgentRepository agentRepository;
    private final MissionRepository missionRepository;
    private final ObjectMapper objectMapper;

    @Value("${nexus.security.heartbeat-timeout-seconds:120}")
    private long heartbeatTimeoutSeconds;

    public AgentService(AgentRepository agentRepository, MissionRepository missionRepository,
                         ObjectMapper objectMapper) {
        this.agentRepository = agentRepository;
        this.missionRepository = missionRepository;
        this.objectMapper = objectMapper;
    }

    public AgentRegisterResponse registerAgent(AgentRegisterRequest request) {
        String apiKey = ApiKeyGenerator.generateKey();
        String apiKeyHash = ApiKeyGenerator.hash(apiKey);
        String capabilitiesJson = writeJson(request.getCapabilities());

        Agent agent = new Agent(request.getName(), request.getVersion(), apiKeyHash, capabilitiesJson);
        agent = agentRepository.save(agent);

        log.info("Agente registrado: {}", agent.getName());
        return new AgentRegisterResponse(agent.getId(), agent.getName(), apiKey);
    }

    public AgentStatusResponse processHeartbeat(String apiKey) {
        Agent agent = getAgentByApiKey(apiKey);
        agent.setLastHeartbeat(Instant.now());
        agent.setStatus(Agent.AgentStatus.ONLINE);
        agentRepository.save(agent);
        return toStatusResponse(agent);
    }

    public List<AgentStatusResponse> getAllAgents() {
        return agentRepository.findAll().stream()
                .map(this::toStatusResponse)
                .collect(Collectors.toList());
    }

    public Agent getAgentByApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AgentNotFoundException("Header X-Agent-Key ausente");
        }
        String hash = ApiKeyGenerator.hash(apiKey);
        return agentRepository.findByApiKeyHash(hash)
                .orElseThrow(() -> new AgentNotFoundException("API Key invalido o agente no encontrado"));
    }

    public Agent getAgentByName(String name) {
        return agentRepository.findByName(name)
                .orElseThrow(() -> new AgentNotFoundException("Agente no encontrado: " + name));
    }

    @Scheduled(fixedRateString = "${nexus.security.scheduler-fixed-rate-ms:60000}")
    public void markOfflineAgents() {
        Instant threshold = Instant.now().minus(heartbeatTimeoutSeconds, ChronoUnit.SECONDS);
        List<Agent> stale = agentRepository.findByStatusAndLastHeartbeatBefore(Agent.AgentStatus.ONLINE, threshold);
        for (Agent agent : stale) {
            agent.setStatus(Agent.AgentStatus.OFFLINE);
            agentRepository.save(agent);
            log.warn("Agente marcado OFFLINE por inactividad: {}", agent.getName());
        }
    }

    private AgentStatusResponse toStatusResponse(Agent agent) {
        long pending = missionRepository
                .findByAgentNameAndStatusOrderByCreatedAtAsc(agent.getName(), Mission.MissionStatus.PENDING)
                .size();

        List<String> capabilities = readJsonList(agent.getCapabilities());

        return new AgentStatusResponse(
                agent.getId(),
                agent.getName(),
                agent.getVersion(),
                agent.getStatus().name(),
                agent.getLastHeartbeat(),
                agent.getRegisteredAt(),
                pending,
                capabilities
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar el payload a JSON", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> readJsonList(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
