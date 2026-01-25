package com.example.pairstatusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PairStatusResponse {

    private UUID userId;
    private String state;          // NONE / WAITING / PAIRED
    private UUID pairId;
    private String joinCode;
    private UUID partnerUserId;

    // 未参加
    public static PairStatusResponse none(UUID userId) {
        return new PairStatusResponse(
                userId,
                "NONE",
                null,
                null,
                null
        );
    }

    // 相手待ち
    public static PairStatusResponse waiting(UUID userId, UUID pairId, String joinCode) {
        return new PairStatusResponse(
                userId,
                "WAITING",
                pairId,
                joinCode,
                null
        );
    }

    // ペア成立
    public static PairStatusResponse paired(
            UUID userId,
            UUID pairId,
            String joinCode,
            UUID partnerUserId
    ) {
        return new PairStatusResponse(
                userId,
                "PAIRED",
                pairId,
                joinCode,
                partnerUserId
        );
    }
}