// package com.example.pairstatusapi.repository;

// import com.example.pairstatusapi.entity.UserEntity;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.Optional;
// import java.util.UUID;

// public interface UserRepository extends JpaRepository<UserEntity, UUID> {

//     long countByPairId(UUID pairId);

//     Optional<UserEntity> findFirstByPairIdAndIdNot(UUID pairId, UUID id);

//     // ✅ 追加：emailでユーザーを引けるようにする
//     Optional<UserEntity> findByEmail(String email);

//     boolean existsByEmail(String email);
// }

package com.example.pairstatusapi.repository;

import com.example.pairstatusapi.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    long countByPairId(UUID pairId);
    Optional<UserEntity> findFirstByPairIdAndIdNot(UUID pairId, UUID id);

    // ✅ 追加
    Optional<UserEntity> findByEmail(String email);
}