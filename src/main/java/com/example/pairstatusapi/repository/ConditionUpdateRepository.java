package com.example.pairstatusapi.repository;

import com.example.pairstatusapi.entity.ConditionUpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConditionUpdateRepository extends JpaRepository<ConditionUpdateEntity, UUID> {

    Optional<ConditionUpdateEntity> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);
}