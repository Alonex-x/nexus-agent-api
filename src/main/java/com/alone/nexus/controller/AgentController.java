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

/** Endpoints for agent registration, heartbeat, and status. */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Agents", description = "Registration and monitoring of software agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @Operation(summary = "Registers a new agent and returns its API Key")
    @PostMapping("/agents/register")
    public ResponseEntity<AgentRegisterResponse> register(@Valid @RequestBody AgentRegisterRequest request) {
        AgentRegisterResponse response = agentService.registerAgent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Reports an agent's periodic heartbeat")
    @PostMapping("/agents/heartbeat")
    public ResponseEntity<AgentStatusResponse> heartbeat(@RequestHeader("X-Agent-Key") String apiKey) {
        return ResponseEntity.ok(agentService.processHeartbeat(apiKey));
    }

    @Operation(summary = "Returns the status of all registered agents")
    @GetMapping("/agents/status")
    public ResponseEntity<List<AgentStatusResponse>> getAllAgents() {
        return ResponseEntity.ok(agentService.getAllAgents());
    }
}
