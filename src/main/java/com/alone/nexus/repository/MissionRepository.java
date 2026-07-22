package com.alone.nexus.repository;

import com.alone.nexus.model.Mission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MissionRepository extends JpaRepository<Mission, UUID> {

    List<Mission> findByAgentNameAndStatusOrderByCreatedAtAsc(String agentName, Mission.MissionStatus status);

    /**
     * Recupera las misiones PENDING de un agente.
     * Se eliminó el bloqueo pesimista para compatibilidad con H2 en desarrollo.
     */
    @Query("SELECT m FROM Mission m WHERE m.agentName = :agentName AND m.status = 'PENDING' ORDER BY m.createdAt ASC")
    List<Mission> findPendingMissionsWithLock(@Param("agentName") String agentName);

    /** Devuelve las misiones mas recientes, de cualquier agente y estado, para el endpoint /missions/recent. */
    List<Mission> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
