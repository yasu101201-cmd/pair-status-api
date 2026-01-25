package com.example.pairstatusapi.config;

import com.example.pairstatusapi.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    
@Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getServletPath();
    String method = request.getMethod();

    if ("OPTIONS".equalsIgnoreCase(method)) return true;

    // ✅ login など「token無しで呼ぶもの」だけ除外
    return path.equals("/auth/login")
        || path.equals("/auth/register")  // あるなら
        || path.equals("/health")
        || path.startsWith("/h2-console/");
}

@Override
protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
) throws ServletException, IOException {

    String path = request.getServletPath();
    String authHeader = request.getHeader("Authorization");

    System.out.println("PATH=" + path);
    System.out.println("AUTH_HEADER=" + authHeader);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
        System.out.println("=> no bearer token");
        filterChain.doFilter(request, response);
        return;
    }

    String token = authHeader.substring(7);
    System.out.println("TOKEN=" + token);

    String userId = null;

    if ("/auth/refresh".equals(path)) {
        // ✅ refreshだけは期限切れでも userId を拾う
        userId = jwtUtil.extractUserIdAllowExpired(token);
        System.out.println("REFRESH_USER_ID=" + userId);
    } else {
        boolean valid = jwtUtil.isValid(token);
        System.out.println("TOKEN_VALID=" + valid);

        if (!valid) {
            System.out.println("=> invalid token pass");
            filterChain.doFilter(request, response);
            return;
        }

        userId = jwtUtil.extractUserId(token);
        System.out.println("USER_ID=" + userId);
    }

    UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authentication);

    System.out.println("AUTH_SET=" + SecurityContextHolder.getContext().getAuthentication());

    filterChain.doFilter(request, response);
}
}