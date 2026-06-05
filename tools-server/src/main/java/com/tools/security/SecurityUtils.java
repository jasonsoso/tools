package com.tools.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 上下文工具类，用于在 Controller/Service 中获取当前登录用户信息。
 * <p>
 * 依赖 {@link JwtAuthFilter} 在请求处理前将用户认证信息注入 {@link SecurityContextHolder}。
 * 如果未经过认证过滤器（如公开接口），调用 {@link #getCurrentUserId()} 将抛出异常。
 */
public class SecurityUtils {

    private SecurityUtils() {
        // 工具类，禁止实例化
    }

    /**
     * 从安全上下文中获取当前登录用户的 ID。
     * <p>
     * 注意：在 {@link JwtAuthFilter} 中，我们将用户 ID 存入了
     * {@link org.springframework.security.core.userdetails.User#getUsername()}，
     * 因此这里通过 {@code Long.parseLong(user.getUsername())} 取回用户 ID。
     *
     * @return 当前登录用户的 ID
     * @throws IllegalStateException 如果请求未被认证（没有有效的 JWT Token）
     */
    public static Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User user) {
            // user.getUsername() 实际存储的是用户 ID（见 UserDetailsServiceImpl）
            return Long.parseLong(user.getUsername());
        }
        throw new IllegalStateException("未认证的用户");
    }
}
