package com.alone.nexus.controller;

import com.alone.nexus.dto.MissionCreateRequest;
import com.alone.nexus.dto.MissionReportRequest;
import com.alone.nexus.dto.MissionResponse;
import com.alone.nexus.service.MissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/** Endpoints for mission creation, assignment, and reporting. */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Missions", description = "Mission queue for agents (Deck)")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @Operation(summary = "Creates a new mission for an agent")
    @PostMapping("/missions")
    public ResponseEntity<MissionResponse> createMission(@Valid @RequestBody MissionCreateRequest request) {
        MissionResponse response = missionService.createMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Returns and assigns pending missions for an agent")
    @GetMapping("/missions/pending")
    public ResponseEntity<List<MissionResponse>> getPendingMissions(
            @RequestParam String agent,
            @RequestHeader("X-Agent-Key") String apiKey) {
        return ResponseEntity.ok(missionService.getPendingMissions(agent));
    }

    @Operation(summary = "Reports the final result of a mission")
    @PostMapping("/missions/{id}/report")
    public ResponseEntity<MissionResponse> reportMission(
            @PathVariable UUID id,
            @Valid @RequestBody MissionReportRequest request) {
        return ResponseEntity.ok(missionService.reportMissionResult(id, request));
    }

    @Operation(summary = "Returns the most recent missions, unfiltered by agent")
    @GetMapping("/missions/recent")
    public ResponseEntity<List<MissionResponse>> getRecentMissions(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(missionService.getRecentMissions(limit));
    }
}
