package com.macro.mall.portal.component;

import com.macro.mall.security.util.PortalJwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Portal模块专用JWT认证过滤器
 * 专门处理会员用户的JWT认证
 * @author Claude
 * @since 2025-09-15
 */
@Component("portalJwtAuthenticationTokenFilter")
public class PortalJwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PortalJwtAuthenticationTokenFilter.class);

    @Autowired
    @Qualifier("portalJwtTokenUtil")
    private PortalJwtTokenUtil portalJwtTokenUtil;

    @Autowired
    @Qualifier("portalUserDetailsService")
    private UserDetailsService portalUserDetailsService;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String authHeader = request.getHeader(this.tokenHeader);
        LOGGER.debug("Portal JWT - 请求头: {}, tokenHeader: {}, tokenHead: {}", authHeader, this.tokenHeader, this.tokenHead);

        if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
            // 提取JWT token (去掉 "Bearer " 前缀)
            String authToken = authHeader.substring(this.tokenHead.length()).trim();

            if (authToken.isEmpty()) {
                LOGGER.warn("Portal JWT - Token为空");
                chain.doFilter(request, response);
                return;
            }

            LOGGER.debug("Portal JWT - 提取的token: {}", authToken.substring(0, Math.min(authToken.length(), 20)) + "...");

            try {
                // 从token中获取用户名
                String username = portalJwtTokenUtil.getUsernameFromToken(authToken);
                LOGGER.debug("Portal JWT - 正在验证用户: {}", username);

                // 如果用户名存在且当前没有认证信息
                if (username != null && !username.trim().isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // 加载用户详情
                    UserDetails userDetails = this.portalUserDetailsService.loadUserByUsername(username);
                    LOGGER.debug("Portal JWT - 已加载用户详情: {}", userDetails.getUsername());

                    // 验证token有效性
                    if (portalJwtTokenUtil.validateAccessToken(authToken)) {
                        // 创建认证对象
                        UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                        // 设置到安全上下文
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // 验证SecurityContext确实被设置
                        boolean hasAuth = SecurityContextHolder.getContext().getAuthentication() != null;
                        LOGGER.info("Portal JWT - 用户认证成功: {}, SecurityContext已设置: {}", username, hasAuth);
                    } else {
                        LOGGER.warn("Portal JWT - Token验证失败: {}", username);
                    }
                } else {
                    LOGGER.debug("Portal JWT - 用户名为空或已认证，跳过验证: username={}, hasAuth={}",
                        username, SecurityContextHolder.getContext().getAuthentication() != null);
                }
            } catch (Exception e) {
                LOGGER.error("Portal JWT - Token解析失败: {}", e.getMessage(), e);
            }
        }

        // 继续过滤链
        chain.doFilter(request, response);
    }
}