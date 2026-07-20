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

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Eventos", description = "Feed de eventos reportados por los agentes")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(summary = "Recibe un evento de un agente (sujeto a rate limit)")
    @PostMapping("/events")
    public ResponseEntity<EventResponse> receiveEvent(
            @Valid @RequestBody EventRequest request,
            @RequestHeader("X-Agent-Key") String apiKey) {
        EventResponse response = eventService.receiveEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Devuelve los eventos mas recientes")
    @GetMapping("/events/recent")
    public ResponseEntity<List<EventResponse>> getRecentEvents(
            @RequestParam(defaultValue = "20") int limit) {
        return ResponseEntity.ok(eventService.getRecentEvents(limit));
    }
}
