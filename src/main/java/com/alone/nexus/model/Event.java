package com.alone.nexus.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "events", indexes = {
    @Index(name = "idx_events_timestamp", columnList = "timestamp DESC")
})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "agent_name")
    private String agentName;

    @Column(nullable = false)
    private String type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String data;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();

    public Event() {}

    public Event(String agentName, String type, String data) {
        this.agentName = agentName;
        this.type = type;
        this.data = data;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
