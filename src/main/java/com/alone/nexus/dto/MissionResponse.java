package com.alone.nexus.dto;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class MissionResponse {

    private UUID id;
    private String agentName;
    private String action;
    private String status;
    private Map<String, Object> params;
    private Map<String, Object> result;
    private Instant createdAt;
    private Instant assignedAt;
    private Instant completedAt;

    public MissionResponse() {}

    public MissionResponse(UUID id, String agentName, String action, String status,
                            Map<String, Object> params, Map<String, Object> result,
                            Instant createdAt, Instant assignedAt, Instant completedAt) {
        this.id = id;
        this.agentName = agentName;
        this.action = action;
        this.status = status;
        this.params = params;
        this.result = result;
        this.createdAt = createdAt;
        this.assignedAt = assignedAt;
        this.completedAt = completedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
    public Map<String, Object> getResult() { return result; }
    public void setResult(Map<String, Object> result) { this.result = result; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
