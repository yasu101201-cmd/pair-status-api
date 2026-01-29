package com.example.pairstatusapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "pairs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PairEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String joinCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PairState state;

    @Column(name = "user_id_1", nullable = false)
    private UUID userId1;

    @Column(name = "user_id_2")
    private UUID userId2;

    public enum PairState {
        WAITING,
        PAIRED
    }
}