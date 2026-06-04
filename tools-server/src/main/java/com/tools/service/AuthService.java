package com.tools.service;

import com.tools.common.ApiResponse;
import com.tools.dto.LoginRequest;
import com.tools.dto.LoginResponse;
import com.tools.dto.RegisterRequest;
import com.tools.entity.User;
import com.tools.repository.UserRepository;
import com.tools.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public ApiResponse<LoginResponse> register(RegisterRequest req) {
        if (userRepository.findByUsername(req.getUsername()) != null) {
            return ApiResponse.error(400, "用户名已存在");
        }
        if (userRepository.findByEmail(req.getEmail()) != null) {
            return ApiResponse.error(400, "邮箱已被注册");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        LoginResponse loginResponse = new LoginResponse(token, user.getId(), user.getUsername());
        return ApiResponse.success(loginResponse);
    }

    public ApiResponse<LoginResponse> login(LoginRequest req) {
        User user = userRepository.findByUsername(req.getUsername());
        if (user == null) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            return ApiResponse.error(401, "用户名或密码错误");
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());

        LoginResponse loginResponse = new LoginResponse(token, user.getId(), user.getUsername());
        return ApiResponse.success(loginResponse);
    }
}
