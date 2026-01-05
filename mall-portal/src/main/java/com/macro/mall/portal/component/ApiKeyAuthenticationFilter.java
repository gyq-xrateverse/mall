package com.macro.mall.portal.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * API Key 认证过滤器
 * 用于beilv-agent等外部系统调用积分管理接口的认证
 * Created by code-executor on 2026-01-05.
 */
@Slf4j
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";
    private static final String API_KEY_PARAM = "apiKey";

    @Value("${mall.api-key:default-api-key-please-change-in-production}")
    private String validApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 只处理积分管理接口
        String requestPath = request.getRequestURI();
        if (!requestPath.startsWith("/api/credits/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取API Key（优先从Header获取，其次从参数获取）
        String apiKey = request.getHeader(API_KEY_HEADER);
        if (!StringUtils.hasText(apiKey)) {
            apiKey = request.getParameter(API_KEY_PARAM);
        }

        // 验证API Key
        if (StringUtils.hasText(apiKey) && apiKey.equals(validApiKey)) {
            log.debug("API Key验证成功: path={}", requestPath);

            // 创建一个虚拟的系统用户认证信息
            UserDetails userDetails = User.withUsername("api-system")
                    .password("")
                    .authorities(Collections.emptyList())
                    .build();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 设置到Security上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.info("API Key认证成功: path={}", requestPath);
        } else if (StringUtils.hasText(apiKey)) {
            log.warn("API Key验证失败: path={}, invalidKey={}", requestPath, apiKey);
        }

        filterChain.doFilter(request, response);
    }
}
