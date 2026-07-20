package com.alone.nexus.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class EventResponse {

    private UUID id;
    private String agentName;
    private String type;
    private Map<String, Object> data;
    private Instant timestamp;

    public EventResponse() {}

    public EventResponse(UUID id, String agentName, String type, Map<String, Object> data, Instant timestamp) {
        this.id = id;
        this.agentName = agentName;
        this.type = type;
        this.data = data;
        this.timestamp = timestamp;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
