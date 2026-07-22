package com.alone.nexus.controller;

import com.alone.nexus.dto.EventRequest;
import com.alone.nexus.dto.EventResponse;
import com.alone.nexus.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Endpoints for event ingestion and querying (feed "Flux"). */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Events", description = "Event feed reported by agents")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Receives an event from an agent (subject to rate limit)")
    @PostMapping("/events")
    public ResponseEntity<EventResponse> receiveEvent(
            @Valid @RequestBody EventRequest request,
            @RequestHeader("X-Agent-Key") String apiKey) {
        EventResponse response = eventService.receiveEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Returns the most recent events")
    @GetMapping("/events/recent")
    public ResponseEntity<List<EventResponse>> getRecentEvents(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(eventService.getRecentEvents(limit));
    }
}
