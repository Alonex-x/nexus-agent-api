package com.alone.nexus.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AgentStatusResponse {

    private UUID id;
    private String name;
    private String version;
    private String status;
    private Instant lastHeartbeat;
    private Instant registeredAt;
    private long pendingMissionCount;
    private List<String> capabilities;

    public AgentStatusResponse() {}

    public AgentStatusResponse(UUID id, String name, String version, String status,
                                Instant lastHeartbeat, Instant registeredAt,
                                long pendingMissionCount, List<String> capabilities) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.status = status;
        this.lastHeartbeat = lastHeartbeat;
        this.registeredAt = registeredAt;
        this.pendingMissionCount = pendingMissionCount;
        this.capabilities = capabilities;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }
    public Instant getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(Instant registeredAt) { this.registeredAt = registeredAt; }
    public long getPendingMissionCount() { return pendingMissionCount; }
    public void setPendingMissionCount(long pendingMissionCount) { this.pendingMissionCount = pendingMissionCount; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
}
