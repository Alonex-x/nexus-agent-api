package com.alone.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/** Data for an event reported by an agent. */
public class EventRequest {

    @NotBlank(message = "Issuing agent name is required")
    private String agent;

    @NotBlank(message = "Event type is required")
    private String type;

    @NotNull(message = "Event body (data) is required")
    private Map<String, Object> data;

    public EventRequest() {}

    public String getAgent() { return agent; }
    public void setAgent(String agent) { this.agent = agent; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
