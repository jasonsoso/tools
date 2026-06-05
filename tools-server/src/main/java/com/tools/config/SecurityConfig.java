package com.tools.config;

import com.tools.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.nio.charset.StandardCharsets;

/**
 * Spring Security 核心配置类。
 * <p>
 * 安全策略：
 * <ul>
 *   <li><b>无状态会话</b>：不使用 HTTP Session，每次请求都通过 JWT Token 认证</li>
 *   <li><b>JWT 过滤器</b>：在 UsernamePasswordAuthenticationFilter 之前注入 {@link JwtAuthFilter}</li>
 *   <li><b>认证接口公开</b>：/api/auth/** 无需认证即可访问</li>
 *   <li><b>其他接口需认证</b>：其余所有接口必须携带有效 JWT Token</li>
 *   <li><b>401/403 返回 JSON</b>：认证失败和权限不足时返回统一 JSON 格式而非默认重定向页面</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 配置安全过滤链。
     * <p>
     * 顺序：CORS 处理 → CSRF 禁用 → 无状态会话 → URL 授权规则 → 异常处理 → JWT 过滤器
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS 使用 CorsConfig 中定义的规则
                .cors(cors -> {
                })
                // 无状态 API 不需要 CSRF 保护（CSRF 主要威胁基于 Cookie 的会话）
                .csrf(csrf -> csrf.disable())
                // 不使用服务端 Session，每次请求独立认证
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 注册和登录接口公开访问
                        .requestMatchers("/api/auth/**").permitAll()
                        // 其他所有接口需要认证
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        // 401：未携带 Token 或 Token 无效时返回 JSON
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("{\"code\":401,\"message\":\"未认证，请登录\",\"data\":null}");
                        })
                        // 403：已认证但无权访问时返回 JSON
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                            response.getWriter().write("{\"code\":403,\"message\":\"无权访问\",\"data\":null}");
                        }))
                // 在 Spring Security 默认的认证过滤器之前插入 JWT 过滤器
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 密码编码器，使用 BCrypt 算法对密码进行不可逆哈希。
     * <p>
     * BCrypt 内置盐值（salt），每次加密结果不同，有效防止彩虹表攻击。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager Bean，供 Controller 或 Service 中手动认证时使用。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
