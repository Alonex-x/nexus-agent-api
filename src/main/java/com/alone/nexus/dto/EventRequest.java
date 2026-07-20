package com.alone.nexus.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class EventRequest {

    @NotBlank(message = "El nombre del agente emisor es obligatorio")
    private String agent;

    @NotBlank(message = "El tipo de evento es obligatorio")
    private String type;

    @NotNull(message = "El cuerpo del evento (data) es obligatorio")
    private Map<String, Object> data;

    public EventRequest() {}

    public String getAgent() { return agent; }
    public void setAgent(String agent) { this.agent = agent; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}
