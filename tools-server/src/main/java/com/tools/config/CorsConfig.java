package com.tools.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS 跨域配置。
 * <p>
 * 允许前端开发服务器（http://localhost:5173）跨域访问后端 API。
 * 生产环境部署在同一域名下时此配置无实际作用，但保留以支持分离部署场景。
 */
@Configuration
public class CorsConfig {

    /**
     * 创建 CORS 过滤器，配置允许的来源、方法和请求头。
     * <p>
     * 使用 {@code setAllowedOriginPatterns} 而非 {@code setAllowedOrigins}
     * 是为了支持 {@code allowCredentials(true)} —— 两者不能同时使用通配符 {@code *}。
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 仅允许前端开发服务器来源
        config.setAllowedOriginPatterns(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // 允许携带 Cookie / Authorization 头
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
