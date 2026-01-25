package com.example.pairstatusapi.controller;

import com.example.pairstatusapi.dto.CreateUserResponse;
import com.example.pairstatusapi.entity.TokenEntity;
import com.example.pairstatusapi.entity.UserEntity;
import com.example.pairstatusapi.repository.TokenRepository;
import com.example.pairstatusapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    // ユーザー作成（tokenも発行して返す）
    @PostMapping
    public CreateUserResponse create() {
        // 1) user作成
        UserEntity u = new UserEntity();
        UserEntity saved = userRepository.save(u);

        // 2) token発行（最小でOK）
        String token = UUID.randomUUID().toString();

        // 3) token保存
        tokenRepository.save(new TokenEntity(token, saved.getId(), Instant.now()));

        // 4) userId + token を返す
        return new CreateUserResponse(saved.getId(), token);
    }

    // ユーザー一覧（必要なら残す）
    @GetMapping
    public List<UserEntity> list() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public UserEntity get(@PathVariable UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }
}