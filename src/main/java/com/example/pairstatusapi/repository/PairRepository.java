package com.example.pairstatusapi.repository;

import com.example.pairstatusapi.entity.PairEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;


public interface PairRepository extends JpaRepository<PairEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PairEntity> findByJoinCode(String joinCode);

    Optional<PairEntity> findFirstByUserId1OrUserId2(UUID userId1, UUID userId2);
}