package com.alone.nexus.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flujoCompletoDeEvento() throws Exception {
        String registerBody = """
                {
                  "name": "flux-agent-v1",
                  "version": "1.0.0",
                  "capabilities": ["monitor"]
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/v1/agents/register")
                        .contentType("application/json")
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String apiKey = registerJson.get("apiKey").asText();

        String eventBody = """
                {
                  "agent": "flux-agent-v1",
                  "type": "heartbeat_log",
                  "data": {"cpu": 12.5, "mem": 340}
                }
                """;

        mockMvc.perform(post("/api/v1/events")
                        .header("X-Agent-Key", apiKey)
                        .contentType("application/json")
                        .content(eventBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.agentName").value("flux-agent-v1"));

        mockMvc.perform(get("/api/v1/events/recent")
                        .param("limit", "10")
                        .header("X-Agent-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("heartbeat_log"));
    }
}
