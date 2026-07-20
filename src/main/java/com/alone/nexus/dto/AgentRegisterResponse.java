package com.alone.nexus.dto;

import java.util.UUID;

public class AgentRegisterResponse {

    private UUID id;
    private String name;
    private String apiKey;

    public AgentRegisterResponse() {}

    public AgentRegisterResponse(UUID id, String name, String apiKey) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
}
