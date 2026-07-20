package com.alone.nexus.service;

import com.alone.nexus.config.RateLimitConfig;
import com.alone.nexus.dto.EventRequest;
import com.alone.nexus.dto.EventResponse;
import com.alone.nexus.exception.TooManyRequestsException;
import com.alone.nexus.model.Event;
import com.alone.nexus.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RateLimitConfig rateLimitConfig;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventService = new EventService(eventRepository, rateLimitConfig, new ObjectMapper());
    }

    private Bucket bucketConCapacidad(long capacity) {
        Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Test
    void recibirEvento_dentroDelLimite_seGuarda() {
        EventRequest request = new EventRequest();
        request.setAgent("guardian-v1");
        request.setType("integrity_check");
        request.setData(Map.of("score", 99));

        when(rateLimitConfig.resolveBucket("guardian-v1")).thenReturn(bucketConCapacidad(60));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> {
            Event e = inv.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        EventResponse response = eventService.receiveEvent(request);

        assertThat(response.getAgentName()).isEqualTo("guardian-v1");
        assertThat(response.getType()).isEqualTo("integrity_check");
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void recibirEvento_superandoElLimite_lanzaExcepcion() {
        EventRequest request = new EventRequest();
        request.setAgent("guardian-v1");
        request.setType("integrity_check");
        request.setData(Map.of("score", 99));

        Bucket bucket = bucketConCapacidad(1);
        bucket.tryConsume(1);
        when(rateLimitConfig.resolveBucket("guardian-v1")).thenReturn(bucket);

        assertThatThrownBy(() -> eventService.receiveEvent(request))
                .isInstanceOf(TooManyRequestsException.class);

        verify(eventRepository, never()).save(any());
    }

    @Test
    void obtenerRecientes_devuelveListaOrdenada() {
        Event event = new Event("sniffer-v1", "log", "{}");
        event.setId(UUID.randomUUID());

        when(eventRepository.findAllByOrderByTimestampDesc(any())).thenReturn(List.of(event));

        List<EventResponse> events = eventService.getRecentEvents(20);

        assertThat(events).hasSize(1);
        assertThat(events.get(0).getAgentName()).isEqualTo("sniffer-v1");
    }
}
