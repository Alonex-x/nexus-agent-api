package com.alone.nexus.repository;

import com.alone.nexus.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {

    Optional<Agent> findByApiKeyHash(String apiKeyHash);

    Optional<Agent> findByName(String name);

    List<Agent> findByStatus(Agent.AgentStatus status);

    List<Agent> findByStatusAndLastHeartbeatBefore(Agent.AgentStatus status, Instant threshold);
}
