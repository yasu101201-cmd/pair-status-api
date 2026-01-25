// package com.example.pairstatusapi.controller;

// import com.example.pairstatusapi.entity.UserEntity;
// import com.example.pairstatusapi.repository.UserRepository;
// import com.example.pairstatusapi.security.JwtUtil;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.Map;
// import java.util.UUID;

// @RestController
// @RequestMapping("/auth")
// public class AuthController {

//     private final JwtUtil jwtUtil;
//     private final UserRepository userRepository;

//     public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
//         this.jwtUtil = jwtUtil;
//         this.userRepository = userRepository;
//     }

//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody LoginRequest request) {

//         // ① MVP用：認証チェック（仮）
//         if (!"password".equals(request.getPassword())) {
//             return ResponseEntity.status(401).body("Invalid credentials");
//         }

//         // ② 2人分のユーザーを固定で用意（動作確認用）
//         UUID userId;
//         if ("test@example.com".equals(request.getEmail())) {
//             userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
//         } else if ("test2@example.com".equals(request.getEmail())) {
//             userId = UUID.fromString("22222222-2222-2222-2222-222222222222");
//         } else {
//             return ResponseEntity.status(401).body("Unknown user email");
//         }

//         // ③ ★重要：ユーザーがDBに無ければ作る（これが「DB対応」）
//         userRepository.findById(userId).orElseGet(() -> {
//             UserEntity u = new UserEntity();
//             u.setId(userId);     // ← これができない場合は UserEntity 側の修正が必要（後述）
//             return userRepository.save(u);
//         });

//         // ④ JWT発行（subにUUID文字列）
//         String accessToken = jwtUtil.generateAccessToken(userId.toString());

//         return ResponseEntity.ok(Map.of("accessToken", accessToken));
//     }

//     // ===== リクエストDTO =====
//     public static class LoginRequest {
//         private String email;
//         private String password;

//         public String getEmail() { return email; }
//         public void setEmail(String email) { this.email = email; }
//         public String getPassword() { return password; }
//         public void setPassword(String password) { this.password = password; }
//     }
    
// }
package com.example.pairstatusapi.controller;

import com.example.pairstatusapi.entity.UserEntity;
import com.example.pairstatusapi.repository.UserRepository;
import com.example.pairstatusapi.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthController(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        // ① MVP用：認証チェック（仮）
        if (!"password".equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        // ② email を正規化（大文字小文字や空白のゆれを吸収）
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();
        if (email.isBlank()) {
            return ResponseEntity.status(400).body("email required");
        }

        // ③ email でユーザー取得、無ければ作る（UUIDは固定じゃなくランダム）
        UserEntity user = userRepository.findByEmail(email).orElseGet(() -> {
            UserEntity u = new UserEntity();
            u.setId(UUID.randomUUID());
            u.setEmail(email);
            u.setPairId(null);
            return userRepository.save(u);
        });

        // ④ JWT発行（subにUUID文字列）
        String accessToken = jwtUtil.generateAccessToken(user.getId().toString());

        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    // ===== リクエストDTO =====
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    @PostMapping("/refresh")
public ResponseEntity<?> refresh(Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
        return ResponseEntity.status(401).body("Unauthorized");
    }

    String userId = String.valueOf(authentication.getPrincipal());
    String accessToken = jwtUtil.generateAccessToken(userId);

    return ResponseEntity.ok(Map.of("accessToken", accessToken));
}
}