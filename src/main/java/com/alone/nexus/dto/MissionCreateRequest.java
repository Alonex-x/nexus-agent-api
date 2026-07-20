package com.alone.nexus.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public class MissionCreateRequest {

    @NotBlank(message = "El nombre del agente destino es obligatorio")
    private String agentName;

    @NotBlank(message = "La accion de la mision es obligatoria")
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
