package com.alone.nexus.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

/** Data to create a new mission for an agent. */
public class MissionCreateRequest {

    @NotBlank(message = "Target agent name is required")
    private String agentName;

    @NotBlank(message = "Mission action is required")
    private String action;

    private Map<String, Object> params;

    public MissionCreateRequest() {}

    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Map<String, Object> getParams() { return params; }
    public void setParams(Map<String, Object> params) { this.params = params; }
}
