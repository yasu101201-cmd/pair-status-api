package com.example.pairstatusapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "condition_updates")
@Getter @Setter
public class ConditionUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    // ✅ main
    @Enumerated(EnumType.STRING)
    @Column(name = "main_condition", nullable = false)
    private MainCondition mainCondition;

    // ✅ sub（任意）
    @Enumerated(EnumType.STRING)
    @Column(name = "sub_condition")
    private SubCondition subCondition;

    // ✅ note（任意）
    @Column(name = "note", length = 200) // とりあえず200文字など
    private String note;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}