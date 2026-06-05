package com.tools.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 令牌工具类，负责 Token 的生成、解析和验证。
 * <p>
 * 使用 HMAC-SHA 算法对 Token 签名，确保令牌不被篡改。
 * Token 中存储用户 ID（作为 subject）和用户名（作为自定义 claim）。
 */
@Component
public class JwtTokenProvider {

    /** HMAC 签名密钥，由配置文件中的 jwt.secret 派生 */
    private final SecretKey key;

    /** Token 过期时间（毫秒），由配置文件中的 jwt.expiration 指定 */
    private final long expiration;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = expiration;
    }

    /**
     * 为用户生成 JWT Token。
     *
     * @param userId   用户 ID，作为 Token 的 subject
     * @param username 用户名，作为自定义 claim 存储
     * @return 签名的 JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 从 Token 中提取用户 ID。
     * <p>
     * 注意：调用前应先通过 {@link #validateToken} 验证 Token 有效性，
     * 否则可能因 Token 被篡改或过期而抛出异常。
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 Token 中提取用户名（自定义 claim）。
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * 验证 Token 是否有效（签名正确且未过期）。
     *
     * @return true 表示有效，false 表示无效（签名错误、过期或被篡改）
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析并验证 Token 的签名，返回其中存储的 Claims。
     * <p>
     * 如果签名无效、Token 已过期或格式不正确，会抛出异常。
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
