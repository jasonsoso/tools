package com.tools.service;

import com.tools.common.BusinessException;
import com.tools.common.ErrorCode;
import com.tools.entity.User;
import com.tools.repository.UserRepository;
import com.tools.security.JwtTokenProvider;
import com.tools.vo.req.LoginReqVO;
import com.tools.vo.req.RegisterReqVO;
import com.tools.vo.resp.LoginRespVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginRespVO register(RegisterReqVO req) {
        if (userRepository.findByUsername(req.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return new LoginRespVO(token, user.getId(), user.getUsername());
    }

    public LoginRespVO login(LoginReqVO req) {
        User user = userRepository.findByUsername(req.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return new LoginRespVO(token, user.getId(), user.getUsername());
    }
}
