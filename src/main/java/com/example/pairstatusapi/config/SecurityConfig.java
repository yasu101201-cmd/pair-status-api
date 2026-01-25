package com.example.pairstatusapi.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // CSRF: APIなら基本OFF（H2だけ例外）
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(PathRequest.toH2Console())
                .disable()
            )

            // セッションを使わない（JWT前提）
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // H2 console 用
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // CORS を Spring Security 側でも有効化
            .cors(Customizer.withDefaults())

            // ★ここが重要：403の理由を出す
            .exceptionHandling(e -> e
  // ✅ 未ログイン/トークン無効/期限切れ など「認証できない」は 401
  .authenticationEntryPoint((req, res, ex) -> {
    System.out.println("★ENTRY_POINT: " + req.getMethod() + " " + req.getRequestURI());
    System.out.println("★AUTH: " + req.getHeader("Authorization"));
    res.sendError(401);
  })

  // ✅ ログインはできてるが権限不足 は 403
  .accessDeniedHandler((req, res, ex) -> {
    System.out.println("★ACCESS_DENIED: " + req.getMethod() + " " + req.getRequestURI());
    System.out.println("★AUTH: " + req.getHeader("Authorization"));
    System.out.println("★EX: " + ex.getMessage());
    res.sendError(403);
  })
)
            .authorizeHttpRequests(auth -> auth
                // ★CORSプリフライトは全許可（超重要）
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 公開
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/health").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()

                // 画面から叩くAPIを一旦許可（ここは後で authenticated に戻してOK）
                .requestMatchers("/pairs/**", "/conditions/**").authenticated()

                // ★これを必ず許可（403/400の時に /error に飛ぶことがある）
                .requestMatchers("/error").permitAll()

                // その他は認証必須
                .anyRequest().authenticated()
            )

            // JWTフィルタを入れる位置（UsernamePasswordAuthenticationFilterより前）
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}