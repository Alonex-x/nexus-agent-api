package com.alone.nexus.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "missions")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, name = "agent_name")
    private String agentName;

    @Column(nullable = false)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String params;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionStatus status = MissionStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    public enum MissionStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }

    public Mission() {}

    public Mission(String agentName, String action, String params) {
        this.agentName = agentName;
        this.action = action;
        this.params = params;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
    public MissionStatus getStatus() { return status; }
    public void setStatus(MissionStatus status) { this.status = status; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getAssignedAt() { return assignedAt; }
    public void setAssignedAt(Instant assignedAt) { this.assignedAt = assignedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
