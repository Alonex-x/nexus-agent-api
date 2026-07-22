package com.alone.nexus.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/** Data sent by an agent upon completing (or failing) a mission. */
public class MissionReportRequest {

    @NotNull(message = "Final mission status is required")
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

