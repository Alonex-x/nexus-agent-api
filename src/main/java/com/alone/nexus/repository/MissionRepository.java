package com.alone.nexus.repository;

import com.alone.nexus.model.Mission;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.UUID;

public interface MissionRepository extends JpaRepository<Mission, UUID> {

    List<Mission> findByAgentNameAndStatusOrderByCreatedAtAsc(String agentName, Mission.MissionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "0")})
    @Query("SELECT m FROM Mission m WHERE m.agentName = :agentName AND m.status = 'PENDING' ORDER BY m.createdAt ASC")
    List<Mission> findPendingMissionsWithLock(@Param("agentName") String agentName);
}
