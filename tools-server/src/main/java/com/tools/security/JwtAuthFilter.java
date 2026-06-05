package com.tools.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器，在每次请求时拦截并验证 Token。
 * <p>
 * 继承 {@link OncePerRequestFilter} 确保每个请求只过滤一次。
 * 过滤器链执行顺序：请求 → JwtAuthFilter → Controller
 * <p>
 * 验证流程：
 * <ol>
 *   <li>从 Authorization 头提取 Bearer Token</li>
 *   <li>验证 Token 签名和有效期</li>
 *   <li>从 Token 中解析用户名，加载 UserDetails</li>
 *   <li>将认证信息注入 Spring Security 上下文</li>
 * </ol>
 * 即使 Token 无效也不阻断请求——后续由 Spring Security 的授权配置决定是否放行。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    /**
     * 核心过滤逻辑：提取 Token → 验证 → 加载用户 → 注入认证上下文。
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            // 从 Token 中获取用户名，再通过数据库加载完整的用户信息
            String username = jwtTokenProvider.getUsernameFromToken(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 构建认证对象并注入 SecurityContext，后续 Controller 可通过 SecurityUtils 获取当前用户
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 无论 Token 是否有效都继续过滤链，由 SecurityConfig 的授权规则决定访问权限
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 Bearer Token。
     * <p>
     * Authorization 头格式：{@code Bearer <token>}
     *
     * @return Token 字符串，如果没有或格式不正确则返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 去掉 "Bearer " 前缀（7个字符）
        }
        return null;
    }
}
