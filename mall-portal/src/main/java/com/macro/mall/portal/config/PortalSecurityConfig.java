package com.macro.mall.portal.config;

import com.macro.mall.portal.component.PortalJwtAuthenticationTokenFilter;
import com.macro.mall.security.component.RestAuthenticationEntryPoint;
import com.macro.mall.security.component.RestfulAccessDeniedHandler;
import com.macro.mall.security.config.IgnoreUrlsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Portal模块专用Spring Security配置
 * 用于配置Portal用户的JWT认证过滤器链
 * @author Claude
 * @since 2025-09-15
 */
@Configuration
@EnableWebSecurity
@Order(1) // 确保Portal配置优先级高于通用配置
public class PortalSecurityConfig {

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private PortalJwtAuthenticationTokenFilter portalJwtAuthenticationTokenFilter;

    @Bean("portalSecurityFilterChain")
    SecurityFilterChain portalFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // 只处理Portal相关的请求路径
            .securityMatcher("/api/**")
            .authorizeHttpRequests(registry -> {
                //不需要保护的资源路径允许访问
                for (String url : ignoreUrlsConfig.getUrls()) {
                    registry.requestMatchers(url).permitAll();
                }
                //允许跨域请求的OPTIONS请求
                registry.requestMatchers(HttpMethod.OPTIONS).permitAll();
                //Portal模块的认证相关接口允许访问
                registry.requestMatchers("/api/auth/**").permitAll();
                //其他Portal API请求需要身份认证
                registry.anyRequest().access(AuthenticatedAuthorizationManager.authenticated());
            })
            //关闭跨站请求防护
            .csrf(AbstractHttpConfigurer::disable)
            //修改Session生成策略为无状态会话
            .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            //自定义权限拒绝处理类
            .exceptionHandling(configurer ->
                configurer.accessDeniedHandler(restfulAccessDeniedHandler)
                         .authenticationEntryPoint(restAuthenticationEntryPoint))
            //使用Portal专用JWT过滤器
            .addFilterBefore(portalJwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}