package com.example.pairstatusapi.dto;

import java.util.UUID;

public class CreateUserResponse {
    private UUID userId;
    private String token;

    public CreateUserResponse() {}

    public CreateUserResponse(UUID userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public UUID getUserId() { return userId; }
    public String getToken() { return token; }
}