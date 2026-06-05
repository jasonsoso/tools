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

/**
 * 认证服务，处理用户注册和登录的核心业务逻辑。
 * <p>
 * 密码使用 BCrypt 加密存储，登录时通过 {@link PasswordEncoder#matches} 比对明文与哈希。
 * 注册成功或登录成功后直接返回 JWT Token，后续请求通过 Token 识别用户身份。
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册。
     * <p>
     * 校验规则：
     * <ul>
     *   <li>用户名唯一</li>
     *   <li>邮箱唯一</li>
     * </ul>
     * 注册成功后自动登录，直接返回 JWT Token。
     *
     * @param req 注册请求（用户名、邮箱、密码）
     * @return 登录响应（Token + 用户信息）
     * @throws BusinessException 用户名或邮箱已存在
     */
    public LoginRespVO register(RegisterReqVO req) {
        // 检查用户名和邮箱的唯一性
        if (userRepository.findByUsername(req.getUsername()) != null) {
            throw new BusinessException(ErrorCode.USERNAME_EXISTS);
        }
        if (userRepository.findByEmail(req.getEmail()) != null) {
            throw new BusinessException(ErrorCode.EMAIL_EXISTS);
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        // BCrypt 加密密码，数据库中不存储明文
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        userRepository.save(user);

        // 注册成功直接签发 Token，省去用户再登录的步骤
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return new LoginRespVO(token, user.getId(), user.getUsername());
    }

    /**
     * 用户登录。
     * <p>
     * 使用 BCrypt 的 {@code matches} 方法比对密码（不能直接比较哈希字符串，
     * 因为 BCrypt 每次加密产生的盐值不同，同一密码每次生成的哈希也不一样）。
     *
     * @param req 登录请求（用户名、密码）
     * @return 登录响应（Token + 用户信息）
     * @throws BusinessException 用户名不存在或密码错误（统一提示，防止用户枚举）
     */
    public LoginRespVO login(LoginReqVO req) {
        User user = userRepository.findByUsername(req.getUsername());
        if (user == null) {
            // 用户名不存在和密码错误使用相同的错误码，防止恶意用户枚举有效账号
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.BAD_CREDENTIALS);
        }

        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        return new LoginRespVO(token, user.getId(), user.getUsername());
    }
}
