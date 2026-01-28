// src/main/java/.../repository/ConditionUpdateRepository.java
package com.example.pairstatusapi.repository;

import com.example.pairstatusapi.entity.ConditionUpdateEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConditionUpdateRepository extends JpaRepository<ConditionUpdateEntity, UUID> {

    Optional<ConditionUpdateEntity> findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    // ✅ 追加：履歴（Pageableで件数制限）
    List<ConditionUpdateEntity> findByUserIdInOrderByCreatedAtDesc(List<UUID> userIds);
}