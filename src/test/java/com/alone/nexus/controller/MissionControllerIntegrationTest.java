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
class MissionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String registrarAgente(String nombre) throws Exception {
        String registerBody = """
                {
                  "name": "%s",
                  "version": "1.0.0",
                  "capabilities": ["mission-runner"]
                }
                """.formatted(nombre);

        MvcResult result = mockMvc.perform(post("/api/v1/agents/register")
                        .contentType("application/json")
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("apiKey").asText();
    }

    @Test
    void flujoCompletoDeMision() throws Exception {
        String apiKey = registrarAgente("mission-agent-v1");

        String createBody = """
                {
                  "agentName": "mission-agent-v1",
                  "action": "scan_network",
                  "params": {"subnet": "192.168.1.0/24"}
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/missions")
                        .header("X-Agent-Key", apiKey)
                        .contentType("application/json")
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        JsonNode missionJson = objectMapper.readTree(createResult.getResponse().getContentAsString());
        String missionId = missionJson.get("id").asText();

        mockMvc.perform(get("/api/v1/missions/pending")
                        .param("agent", "mission-agent-v1")
                        .header("X-Agent-Key", apiKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"));

        String reportBody = """
                {
                  "status": "COMPLETED",
                  "result": {"hostsFound": 12}
                }
                """;

        mockMvc.perform(post("/api/v1/missions/" + missionId + "/report")
                        .header("X-Agent-Key", apiKey)
                        .contentType("application/json")
                        .content(reportBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.result.hostsFound").value(12));
    }
}
