package com.macro.mall.portal.service;

import java.util.Map;

/**
 * Token服务接口
 * @author Claude
 * @since 2025-09-10
 */
public interface TokenService {
    
    /**
     * 生成访问token
     * @param username 用户名
     * @param userId 用户ID
     * @return 访问token
     */
    String generateAccessToken(String username, Long userId);
    
    /**
     * 生成刷新token
     * @param username 用户名
     * @return 刷新token
     */
    String generateRefreshToken(String username);
    
    /**
     * 生成token对（访问token和刷新token）
     * @param username 用户名
     * @param userId 用户ID
     * @return token对，包含access_token和refresh_token
     */
    Map<String, String> generateTokenPair(String username, Long userId);
    
    /**
     * 刷新访问token
     * @param refreshToken 刷新token
     * @return 新的访问token，如果刷新失败返回null
     */
    String refreshAccessToken(String refreshToken);
    
    /**
     * 验证token是否有效
     * @param token 要验证的token
     * @return 是否有效
     */
    boolean validateToken(String token);
    
    /**
     * 从token中获取用户名
     * @param token token
     * @return 用户名
     */
    String getUsernameFromToken(String token);
    
    /**
     * 从token中获取用户ID
     * @param token token
     * @return 用户ID
     */
    Long getUserIdFromToken(String token);
    
    /**
     * 注销token（添加到黑名单）
     * @param token 要注销的token
     */
    void revokeToken(String token);

    /**
     * 注销token（使用预获取的用户信息，避免重复解析token）
     * @param token 要注销的token
     * @param username 用户名
     * @param userId 用户ID
     */
    void revokeTokenWithUserInfo(String token, String username, Long userId);

    /**
     * 检查token是否已被注销
     * @param token 要检查的token
     * @return 是否已被注销
     */
    boolean isTokenRevoked(String token);
}