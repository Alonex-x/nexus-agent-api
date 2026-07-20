package com.alone.nexus.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

public class MissionReportRequest {

    @NotNull(message = "El estado final de la mision es obligatorio")
    private Status status;

    private Map<String, Object> result;

    public enum Status {
        COMPLETED, FAILED
    }

    public MissionReportRequest() {}

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Map<String, Object> getResult() { return result; }
    public void setResult(Map<String, Object> result) { this.result = result; }
}
