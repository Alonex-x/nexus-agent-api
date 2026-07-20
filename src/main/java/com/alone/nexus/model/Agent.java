package com.alone.nexus.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa un agente de software registrado en el ecosistema
 * (scraper, analizador de red, monitor, etc.).
 */
@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    /** Hash SHA-256 del API Key del agente. Nunca se guarda en texto plano. */
    @Column(nullable = false, name = "api_key_hash")
    private String apiKeyHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status = AgentStatus.ONLINE;

    @Column(nullable = false, name = "last_heartbeat")
    private Instant lastHeartbeat = Instant.now();

    @Column(nullable = false, name = "registered_at")
    private Instant registeredAt = Instant.now();

    /** Lista de capacidades del agente, serializada como JSON. */
    @Column(columnDefinition = "TEXT")
    private String capabilities;

    public enum AgentStatus {
        ONLINE, OFFLINE
    }

    public Agent() {
    }

    public Agent(String name, String version, String apiKeyHash, String capabilities) {
        this.name = name;
        this.version = version;
        this.apiKeyHash = apiKeyHash;
        this.capabilities = capabilities;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApiKeyHash() {
        return apiKeyHash;
    }

    public void setApiKeyHash(String apiKeyHash) {
        this.apiKeyHash = apiKeyHash;
    }

    public AgentStatus getStatus() {
        return status;
    }

    public void setStatus(AgentStatus status) {
        this.status = status;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Instant lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }
}
