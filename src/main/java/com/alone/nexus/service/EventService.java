package com.alone.nexus.service;

import com.alone.nexus.config.RateLimitConfig;
import com.alone.nexus.dto.EventRequest;
import com.alone.nexus.dto.EventResponse;
import com.alone.nexus.exception.TooManyRequestsException;
import com.alone.nexus.model.Event;
import com.alone.nexus.repository.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Business logic for event ingestion and querying ("Flux"). */
@Service
public class EventService {

    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventRepository;
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper;

    public EventService(EventRepository eventRepository, RateLimitConfig rateLimitConfig,
                         ObjectMapper objectMapper) {
        this.eventRepository = eventRepository;
        this.rateLimitConfig = rateLimitConfig;
        this.objectMapper = objectMapper;
    }

    /** Receives and persists an event, applying rate limiting per agent. */
    public EventResponse receiveEvent(EventRequest request) {
        Bucket bucket = rateLimitConfig.resolveBucket(request.getAgent());
        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException(
                    "Event rate limit exceeded for agent: " + request.getAgent());
        }

        Event event = new Event(request.getAgent(), request.getType(), writeJson(request.getData()));
        event = eventRepository.save(event);

        log.debug("Event received from {}: {}", event.getAgentName(), event.getType());
        return toResponse(event);
    }

    /** Returns the N most recent events, ordered from newest to oldest. */
    public List<EventResponse> getRecentEvents(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, 200));
        return eventRepository.findAllByOrderByTimestampDesc(PageRequest.of(0, safeLimit)).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getAgentName(),
                event.getType(),
                readJsonMap(event.getData()),
                event.getTimestamp()
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payload to JSON", e);
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
