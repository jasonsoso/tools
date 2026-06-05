package com.tools.service;

import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.entity.User;
import com.tools.repository.UserRepository;
import com.tools.security.JwtTokenProvider;
import com.tools.vo.req.LoginReqVO;
import com.tools.vo.req.RegisterReqVO;
import com.tools.vo.resp.LoginRespVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        RegisterReqVO req = new RegisterReqVO();
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

        LoginRespVO resp = authService.register(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getToken()).isEqualTo("jwt-token-123");
        assertThat(resp.getUsername()).isEqualTo("newuser");
        assertThat(resp.getUserId()).isEqualTo(100L);
    }

    @Test
    void shouldRejectDuplicateUsername() {
        RegisterReqVO req = new RegisterReqVO();
        req.setUsername("existing");
        req.setEmail("new@example.com");
        req.setPassword("password123");

        User existingUser = new User();
        existingUser.setUsername("existing");

        when(userRepository.findByUsername("existing")).thenReturn(existingUser);

        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCode.USERNAME_EXISTS.getCode())
                .hasMessageContaining("用户名");
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginReqVO req = new LoginReqVO();
        req.setUsername("testuser");
        req.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L, "testuser")).thenReturn("jwt-token-456");

        LoginRespVO resp = authService.login(req);

        assertThat(resp).isNotNull();
        assertThat(resp.getToken()).isEqualTo("jwt-token-456");
        assertThat(resp.getUserId()).isEqualTo(1L);
        assertThat(resp.getUsername()).isEqualTo("testuser");
    }

    @Test
    void shouldRejectWrongPassword() {
        LoginReqVO req = new LoginReqVO();
        req.setUsername("testuser");
        req.setPassword("wrongpassword");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("encodedPassword");

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("code", ErrorCode.BAD_CREDENTIALS.getCode())
                .hasMessageContaining("密码");
    }
}
