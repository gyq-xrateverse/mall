package com.macro.mall.security.config;

import com.macro.mall.security.component.DynamicAuthorizationManager;
import com.macro.mall.security.component.JwtAuthenticationTokenFilter;
import com.macro.mall.security.component.RestAuthenticationEntryPoint;
import com.macro.mall.security.component.RestfulAccessDeniedHandler;
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
 * 管理员模块Spring Security配置，仅用于配置管理员SecurityFilterChain
 * Created by macro on 2019/11/5.
 */
@Configuration
@EnableWebSecurity
@Order(2) // 确保优先级低于Portal配置
public class SecurityConfig {

    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private RestfulAccessDeniedHandler restfulAccessDeniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired(required = false)
    private DynamicAuthorizationManager dynamicAuthorizationManager;

    @Bean("adminSecurityFilterChain")
    SecurityFilterChain adminFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            // 只处理管理员相关的请求路径，排除Portal API
            .securityMatcher(request -> !request.getRequestURI().startsWith("/api/"))
            .authorizeHttpRequests(registry -> {
                //不需要保护的资源路径允许访问
                for (String url : ignoreUrlsConfig.getUrls()) {
                    registry.requestMatchers(url).permitAll();
                }
                //允许跨域请求的OPTIONS请求
                registry.requestMatchers(HttpMethod.OPTIONS).permitAll();
                //任何请求需要身份认证
                registry.anyRequest()
                    //有动态权限配置时添加动态权限管理器
                    .access(dynamicAuthorizationManager==null? AuthenticatedAuthorizationManager.authenticated():dynamicAuthorizationManager);
            })
        //关闭跨站请求防护
        .csrf(AbstractHttpConfigurer::disable)
        //修改Session生成策略为无状态会话
        .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //自定义权限拒绝处理类
        .exceptionHandling(configurer -> configurer.accessDeniedHandler(restfulAccessDeniedHandler).authenticationEntryPoint(restAuthenticationEntryPoint))
        //自定义权限拦截器JWT过滤器
        .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

}
