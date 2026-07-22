package com.alone.nexus.service;

import com.alone.nexus.dto.MissionCreateRequest;
import com.alone.nexus.dto.MissionReportRequest;
import com.alone.nexus.dto.MissionResponse;
import com.alone.nexus.model.Mission;
import com.alone.nexus.repository.EventRepository;
import com.alone.nexus.repository.MissionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionServiceTest {

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private AgentService agentService;

    @Mock
    private EventRepository eventRepository;

    private MissionService missionService;

    @BeforeEach
    void setUp() {
        missionService = new MissionService(missionRepository, agentService, eventRepository, new ObjectMapper());
    }

    @Test
    void crearMision_verificaAgenteYGuardaComoPending() {
        MissionCreateRequest request = new MissionCreateRequest();
        request.setAgentName("scraper-v1");
        request.setAction("scrape_url");
        request.setParams(Map.of("url", "https://example.com"));

        when(missionRepository.save(any(Mission.class))).thenAnswer(inv -> {
            Mission m = inv.getArgument(0);
            m.setId(UUID.randomUUID());
            return m;
        });

        MissionResponse response = missionService.createMission(request);

        verify(agentService).getAgentByName("scraper-v1");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getAgentName()).isEqualTo("scraper-v1");
    }

    @Test
    void obtenerPendientes_lasTransicionaAInProgress() {
        Mission mission = new Mission("scraper-v1", "scrape_url", "{}");
        mission.setId(UUID.randomUUID());

        when(missionRepository.findPendingMissionsWithLock("scraper-v1")).thenReturn(List.of(mission));
        when(missionRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        List<MissionResponse> result = missionService.getPendingMissions("scraper-v1");

        assertThat(result).hasSize(1);
        assertThat(mission.getStatus()).isEqualTo(Mission.MissionStatus.IN_PROGRESS);
        assertThat(mission.getAssignedAt()).isNotNull();
    }

    @Test
    void reportarResultado_actualizaEstadoYGuardaResultado() {
        UUID missionId = UUID.randomUUID();
        Mission mission = new Mission("guardian-v1", "check_integrity", "{}");
        mission.setId(missionId);
        mission.setStatus(Mission.MissionStatus.IN_PROGRESS);

        MissionReportRequest request = new MissionReportRequest();
        request.setStatus(MissionReportRequest.Status.COMPLETED);
        request.setResult(Map.of("ok", true));

        when(missionRepository.findById(missionId)).thenReturn(Optional.of(mission));
        when(missionRepository.save(any(Mission.class))).thenAnswer(inv -> inv.getArgument(0));

        MissionResponse response = missionService.reportMissionResult(missionId, request);

        assertThat(response.getStatus()).isEqualTo("COMPLETED");
        assertThat(mission.getCompletedAt()).isNotNull();
    }
}
