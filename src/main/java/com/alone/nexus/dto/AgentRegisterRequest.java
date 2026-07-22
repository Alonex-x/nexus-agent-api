package com.alone.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Data sent by an agent to register for the first time. */
public class AgentRegisterRequest {

    @NotBlank(message = "Agent name is required")
    private String name;

    @NotBlank(message = "Agent version is required")
    private String version;

    @NotEmpty(message = "Agent must declare at least one capability")
    private List<String> capabilities;

    public AgentRegisterRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public List<String> getCapabilities() { return capabilities; }
    public void setCapabilities(List<String> capabilities) { this.capabilities = capabilities; }
}
