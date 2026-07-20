package com.alone.nexus.controller;

import com.alone.nexus.dto.AgentRegisterRequest;
import com.alone.nexus.dto.AgentRegisterResponse;
import com.alone.nexus.dto.AgentStatusResponse;
import com.alone.nexus.service.AgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Agentes", description = "Registro y monitoreo de agentes de software")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Operation(summary = "Registra un nuevo agente y devuelve su API Key")
    @PostMapping("/agents/register")
    public ResponseEntity<AgentRegisterResponse> register(@Valid @RequestBody AgentRegisterRequest request) {
        AgentRegisterResponse response = agentService.registerAgent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Reporta el heartbeat periodico de un agente")
    @PostMapping("/agents/heartbeat")
    public ResponseEntity<AgentStatusResponse> heartbeat(@RequestHeader("X-Agent-Key") String apiKey) {
        return ResponseEntity.ok(agentService.processHeartbeat(apiKey));
    }

    @Operation(summary = "Devuelve el estado de todos los agentes registrados")
    @GetMapping("/agents/status")
    public ResponseEntity<List<AgentStatusResponse>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }
}
