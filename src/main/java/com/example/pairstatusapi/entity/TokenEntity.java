package com.example.pairstatusapi.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tokens", indexes = {
        @Index(name = "idx_tokens_token", columnList = "token", unique = true),
        @Index(name = "idx_tokens_user_id", columnList = "userId")
})
public class TokenEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String token;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Instant createdAt;

    public TokenEntity() {}

    public TokenEntity(String token, UUID userId, Instant createdAt) {
        this.token = token;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public String getToken() { return token; }
    public UUID getUserId() { return userId; }
    public Instant getCreatedAt() { return createdAt; }
}