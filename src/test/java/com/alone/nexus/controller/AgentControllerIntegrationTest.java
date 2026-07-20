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
class AgentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void flujoCompletoDeRegistroHeartbeatYEstado() throws Exception {
        String registerBody = """
                {
                  "name": "scraper-integration-v1",
                  "version": "1.0.0",
                  "capabilities": ["scrape", "parse"]
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/v1/agents/register")
                        .contentType("application/json")
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("scraper-integration-v1"))
                .andExpect(jsonPath("$.apiKey").isNotEmpty())
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String apiKey = registerJson.get("apiKey").asText();

        mockMvc.perform(post("/api/v1/agents/heartbeat")
                        .header("X-Agent-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONLINE"));

        mockMvc.perform(get("/api/v1/agents/status")
                        .header("X-Agent-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'scraper-integration-v1')]").exists());
    }

    @Test
    void heartbeatSinApiKey_devuelve401() throws Exception {
        mockMvc.perform(post("/api/v1/agents/heartbeat"))
                .andExpect(status().isUnauthorized());
    }
}
