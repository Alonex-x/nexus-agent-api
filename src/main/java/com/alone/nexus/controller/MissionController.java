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

/** Endpoints de creacion, asignacion y reporte de misiones. */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Misiones", description = "Cola de misiones para los agentes (Deck)")
public class MissionController {

    private final MissionService missionService;

    public MissionController(MissionService missionService) {
        this.missionService = missionService;
    }

    @Operation(summary = "Crea una nueva mision para un agente")
    @PostMapping("/missions")
    public ResponseEntity<MissionResponse> createMission(@Valid @RequestBody MissionCreateRequest request) {
        MissionResponse response = missionService.createMission(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Devuelve y asigna las misiones pendientes de un agente")
    @GetMapping("/missions/pending")
    public ResponseEntity<List<MissionResponse>> getPendingMissions(
            @RequestParam String agent,
            @RequestHeader("X-Agent-Key") String apiKey) {
        return ResponseEntity.ok(missionService.getPendingMissions(agent));
    }

    @Operation(summary = "Reporta el resultado final de una mision")
    @PostMapping("/missions/{id}/report")
    public ResponseEntity<MissionResponse> reportMission(
            @PathVariable UUID id,
            @Valid @RequestBody MissionReportRequest request) {
        return ResponseEntity.ok(missionService.reportMissionResult(id, request));
    }

    @Operation(summary = "Devuelve las misiones mas recientes, sin filtrar por agente")
    @GetMapping("/missions/recent")
    public ResponseEntity<List<MissionResponse>> getRecentMissions(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(missionService.getRecentMissions(limit));
    }
}
