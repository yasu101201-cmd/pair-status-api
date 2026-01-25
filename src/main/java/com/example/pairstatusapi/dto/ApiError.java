package com.example.pairstatusapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiError {

    private int status;
    private String message;

    // ★ これがないと of は赤線になる
    public static ApiError of(int status, String message) {
        return new ApiError(status, message);
    }
}