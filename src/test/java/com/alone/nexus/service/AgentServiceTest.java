package com.alone.nexus.service;

import com.alone.nexus.dto.AgentRegisterRequest;
import com.alone.nexus.dto.AgentRegisterResponse;
import com.alone.nexus.dto.AgentStatusResponse;
import com.alone.nexus.exception.AgentNotFoundException;
import com.alone.nexus.model.Agent;
import com.alone.nexus.repository.AgentRepository;
import com.alone.nexus.repository.EventRepository;
import com.alone.nexus.repository.MissionRepository;
import com.alone.nexus.util.ApiKeyGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private EventRepository eventRepository;

    private AgentService agentService;

    @BeforeEach
    void setUp() {
        agentService = new AgentService(agentRepository, missionRepository, eventRepository, new ObjectMapper());
    }

    @Test
    void registrarAgente_creaAgenteYDevuelveApiKeyEnClaro() {
        AgentRegisterRequest request = new AgentRegisterRequest();
        request.setName("scraper-v1");
        request.setVersion("1.0.0");
        request.setCapabilities(List.of("scrape", "parse"));

        when(agentRepository.save(any(Agent.class))).thenAnswer(invocation -> {
            Agent agent = invocation.getArgument(0);
            agent.setId(java.util.UUID.randomUUID());
            return agent;
        });

        AgentRegisterResponse response = agentService.registerAgent(request);

        assertThat(response.getName()).isEqualTo("scraper-v1");
        assertThat(response.getApiKey()).isNotBlank();

        ArgumentCaptor<Agent> captor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(captor.capture());
        assertThat(captor.getValue().getApiKeyHash()).isEqualTo(ApiKeyGenerator.hash(response.getApiKey()));
    }

    @Test
    void heartbeat_actualizaLastHeartbeatYEstadoOnline() {
        Agent agent = new Agent("sniffer-v1", "1.0.0", ApiKeyGenerator.hash("clave-valida"), "[]");
        agent.setStatus(Agent.AgentStatus.OFFLINE);
        agent.setLastHeartbeat(Instant.now().minus(10, ChronoUnit.MINUTES));

        when(agentRepository.findByApiKeyHash(ApiKeyGenerator.hash("clave-valida")))
                .thenReturn(Optional.of(agent));
        when(agentRepository.save(any(Agent.class))).thenAnswer(inv -> inv.getArgument(0));
        when(missionRepository.findByAgentNameAndStatusOrderByCreatedAtAsc(any(), any()))
                .thenReturn(List.of());

        AgentStatusResponse response = agentService.processHeartbeat("clave-valida");

        assertThat(response.getStatus()).isEqualTo("ONLINE");
        assertThat(agent.getStatus()).isEqualTo(Agent.AgentStatus.ONLINE);
    }

    @Test
    void heartbeat_conApiKeyInvalido_lanzaExcepcion() {
        when(agentRepository.findByApiKeyHash(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> agentService.processHeartbeat("clave-invalida"))
                .isInstanceOf(AgentNotFoundException.class);
    }

    @Test
    void marcarOffline_marcaAgentesConHeartbeatVencido() {
        Agent stale = new Agent("guardian-v1", "1.0.0", "hash", "[]");
        stale.setStatus(Agent.AgentStatus.ONLINE);
        stale.setLastHeartbeat(Instant.now().minus(5, ChronoUnit.MINUTES));

        when(agentRepository.findByStatusAndLastHeartbeatBefore(eq(Agent.AgentStatus.ONLINE), any()))
                .thenReturn(List.of(stale));
        when(agentRepository.save(any(Agent.class))).thenAnswer(inv -> inv.getArgument(0));

        agentService.markOfflineAgents();

        assertThat(stale.getStatus()).isEqualTo(Agent.AgentStatus.OFFLINE);
        verify(agentRepository).save(stale);
    }
}
