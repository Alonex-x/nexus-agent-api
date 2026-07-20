package com.alone.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AgentRegisterRequest {

    @NotBlank(message = "El nombre del agente es obligatorio")
    private String name;

    @NotBlank(message = "La version del agente es obligatoria")
    private String version;

    @NotEmpty(message = "El agente debe declarar al menos una capacidad")
    private List<String> capabilities;

    public AgentRegisterRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
}
