package com.example.pairstatusapi.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class MeController {

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        // JwtAuthenticationFilterでprincipalにuserIdを入れている
        String userId = (String) authentication.getPrincipal();
        return Map.of("userId", userId, "authenticated", true);
    }
}