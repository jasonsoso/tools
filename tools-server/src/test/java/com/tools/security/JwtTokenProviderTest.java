package com.tools.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(
                "test-secret-key-that-is-long-enough-for-hs256-algorithm-123",
                3600000L
        );
    }

    @Test
    void shouldGenerateTokenAndExtractUserId() {
        String token = jwtTokenProvider.generateToken(1L, "testuser");

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void shouldValidateValidToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser");

        boolean valid = jwtTokenProvider.validateToken(token);

        assertThat(valid).isTrue();
    }

    @Test
    void shouldRejectInvalidToken() {
        String invalidToken = "this.is.not.a.valid.jwt.token";

        boolean valid = jwtTokenProvider.validateToken(invalidToken);

        assertThat(valid).isFalse();
    }

    @Test
    void shouldExtractUsernameFromToken() {
        String token = jwtTokenProvider.generateToken(1L, "testuser");

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertThat(username).isEqualTo("testuser");
    }
}
