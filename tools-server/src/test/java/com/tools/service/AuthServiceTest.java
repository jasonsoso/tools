package com.tools.service;

import com.tools.common.ApiResponse;
import com.tools.dto.LoginRequest;
import com.tools.dto.LoginResponse;
import com.tools.dto.RegisterRequest;
import com.tools.entity.User;
import com.tools.repository.UserRepository;
import com.tools.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterSuccessfully() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("new@example.com");
        req.setPassword("password123");

        when(userRepository.findByUsername("newuser")).thenReturn(null);
        when(userRepository.findByEmail("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        doAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(100L);
            return null;
        }).when(userRepository).save(any(User.class));
        when(jwtTokenProvider.generateToken(100L, "newuser")).thenReturn("jwt-token-123");

        ApiResponse<LoginResponse> response = authService.register(req);

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getData().getUsername()).isEqualTo("newuser");
        assertThat(response.getData().getUserId()).isEqualTo(100L);
    }

    @Test
    void shouldRejectDuplicateUsername() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existing");
        req.setEmail("new@example.com");
        req.setPassword("password123");

        User existingUser = new User();
        existingUser.setUsername("existing");

        when(userRepository.findByUsername("existing")).thenReturn(existingUser);

        ApiResponse<LoginResponse> response = authService.register(req);

        assertThat(response.getCode()).isEqualTo(400);
        assertThat(response.getMessage()).contains("用户名");
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "testuser")).thenReturn("jwt-token-456");

        ApiResponse<LoginResponse> response = authService.login(req);

        assertThat(response.getCode()).isEqualTo(200);
        assertThat(response.getData()).isNotNull();
        assertThat(response.getData().getToken()).isEqualTo("jwt-token-456");
        assertThat(response.getData().getUserId()).isEqualTo(1L);
        assertThat(response.getData().getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldRejectWrongPassword() {
        LoginRequest req = new LoginRequest();
        req.setUsername("testuser");
        req.setPassword("wrongpassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        ApiResponse<LoginResponse> response = authService.login(req);

        assertThat(response.getCode()).isEqualTo(401);
        assertThat(response.getMessage()).contains("密码");
    }
}
