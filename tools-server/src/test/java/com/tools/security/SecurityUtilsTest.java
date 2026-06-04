package com.tools.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnCurrentUserId() {
        User principal = new User("1", "password", new ArrayList<>());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Long userId = SecurityUtils.getCurrentUserId();

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenNoAuthentication() {
        assertThatThrownBy(SecurityUtils::getCurrentUserId)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("未认证");
    }
}
