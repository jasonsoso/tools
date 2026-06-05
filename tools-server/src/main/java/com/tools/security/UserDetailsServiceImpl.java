package com.tools.security;

import com.tools.entity.User;
import com.tools.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * Spring Security 用户加载服务，从数据库查询用户信息用于认证。
 * <p>
 * 关键设计：{@link org.springframework.security.core.userdetails.User} 的 username 字段
 * 实际存储的是用户 ID（而非用户名），这样在后续通过 {@link SecurityUtils#getCurrentUserId()}
 * 可以直接取回用户 ID，避免重复查库。
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 根据用户名从数据库加载用户信息。
     * <p>
     * 返回的 UserDetails 对象中：
     * <ul>
     *   <li>username = 用户 ID（而非用户名，方便后续直接获取）</li>
     *   <li>password = 数据库中 BCrypt 加密后的密码哈希</li>
     *   <li>authorities = 空列表（当前系统未实现角色权限）</li>
     * </ul>
     *
     * @param username 数据库中的用户名
     * @return Spring Security 的 UserDetails 对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }
        // username 字段存的是用户 ID，参见类级注释
        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPasswordHash(),
                new ArrayList<>());
    }
}
