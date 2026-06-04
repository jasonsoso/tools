package com.tools.security;

import com.tools.entity.User;
import com.tools.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void shouldLoadUserByUsername() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPasswordHash("hashed-password");
        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDetails result = userDetailsService.loadUserByUsername("testuser");

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("1");
        assertThat(result.getPassword()).isEqualTo("hashed-password");
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(null);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("nonexistent"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }
}
