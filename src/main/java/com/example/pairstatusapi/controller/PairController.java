package com.example.pairstatusapi.controller;

import com.example.pairstatusapi.dto.JoinRequest;
import com.example.pairstatusapi.dto.PairStatusResponse;
import com.example.pairstatusapi.entity.PairEntity;
import com.example.pairstatusapi.service.PairService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pairs")
public class PairController {

    private final PairService pairService;

    private UUID currentUserId(Authentication authentication) {
        // JwtAuthenticationFilterで principal に userId(String) を入れている
        String userIdStr = (String) authentication.getPrincipal();
        return UUID.fromString(userIdStr);
    }

    @PostMapping("/create")
    public PairEntity create(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        return pairService.create(userId);
    }

    @PostMapping("/join")
public PairEntity join(@RequestBody JoinRequest req, Authentication auth) {
    UUID userId = UUID.fromString((String) auth.getPrincipal());
    return pairService.join(userId, req.getJoinCode());
}

    @GetMapping("/status")
    public PairStatusResponse status(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        return pairService.status(userId);
    }

    @PostMapping("/leave")
    public void leave(Authentication authentication) {
        UUID userId = currentUserId(authentication);
        pairService.leave(userId);
    }
}