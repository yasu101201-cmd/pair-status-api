package com.example.pairstatusapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // ★環境変数（32文字以上）
    private static final String SECRET =
    System.getenv().getOrDefault("JWT_SECRET", "CHANGE_ME_TO_A_LONG_SECRET_KEY_32CHARS+");
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Access Token の有効期限（例：15分）
    private static final long ACCESS_TOKEN_EXP_MS = 15 * 60 * 1000;
    // private static final long ACCESS_TOKEN_EXP_MS = 10 * 1000; // ← 動作確認用に10秒

    public String generateAccessToken(String userId) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + ACCESS_TOKEN_EXP_MS);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

   public boolean isValid(String token) {
    try {
        Jwts.parserBuilder()
            .setSigningKey(KEY)
            .build()
            .parseClaimsJws(token);
        return true;
    } catch (Exception e) {
        e.printStackTrace(); // ←まずこれで原因を出す
        return false;
    }
   }
public String extractUserIdAllowExpired(String token) {
    try {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    } catch (io.jsonwebtoken.ExpiredJwtException e) {
        return e.getClaims().getSubject();
    }
}
}