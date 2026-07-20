package com.alone.nexus.dto;

import java.time.Instant;

public class ErrorResponse {

    private String error;
    private int status;
    private Instant timestamp;
    private String path;

    public ErrorResponse() {}

    public ErrorResponse(String error, int status, Instant timestamp, String path) {
        this.error = error;
        this.status = status;
        this.timestamp = timestamp;
        this.path = path;
    }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
