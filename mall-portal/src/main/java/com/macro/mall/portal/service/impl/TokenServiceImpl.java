package com.macro.mall.portal.service.impl;

import com.macro.mall.portal.service.TokenService;
import com.macro.mall.portal.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Token服务实现类
 * @author Claude
 * @since 2025-09-10
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private static final String TOKEN_BLACKLIST_PREFIX = "token_blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    @Override
    public String generateAccessToken(String username, Long userId) {
        return jwtTokenUtil.generateTokenWithUserId(username, userId);
    }
    
    @Override
    public String generateRefreshToken(String username) {
        String refreshToken = jwtTokenUtil.generateRefreshToken(username);
        // 将刷新token存储到Redis中，设置过期时间
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
        return refreshToken;
    }
    
    @Override
    public Map<String, String> generateTokenPair(String username, Long userId) {
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", generateAccessToken(username, userId));
        tokenPair.put("refresh_token", generateRefreshToken(username));
        tokenPair.put("token_type", "Bearer");
        tokenPair.put("expires_in", "86400"); // 24小时
        return tokenPair;
    }
    
    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            // 验证刷新token是否有效
            String username = jwtTokenUtil.getUserNameFromToken(refreshToken);
            if (username == null) {
                log.warn("Invalid refresh token");
                return null;
            }
            
            // 检查刷新token是否存在于Redis中
            String key = REFRESH_TOKEN_PREFIX + username;
            String storedRefreshToken = redisTemplate.opsForValue().get(key);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                log.warn("Refresh token not found or mismatched for user: {}", username);
                return null;
            }
            
            // 生成新的访问token
            // 这里需要获取用户ID，可以考虑从refreshToken中获取或从数据库查询
            // 暂时使用null，实际使用时需要完善
            return jwtTokenUtil.generateToken(username);
            
        } catch (Exception e) {
            log.error("Failed to refresh access token", e);
            return null;
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            // 检查token是否已被注销
            if (isTokenRevoked(token)) {
                return false;
            }
            
            // 验证token格式和有效性
            String username = jwtTokenUtil.getUserNameFromToken(token);
            return username != null;
            
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        try {
            return jwtTokenUtil.getUserNameFromToken(token);
        } catch (Exception e) {
            log.error("Failed to get username from token", e);
            return null;
        }
    }
    
    @Override
    public Long getUserIdFromToken(String token) {
        try {
            return jwtTokenUtil.getUserIdFromToken(token);
        } catch (Exception e) {
            log.error("Failed to get user ID from token", e);
            return null;
        }
    }
    
    @Override
    public void revokeToken(String token) {
        try {
            // 将token添加到黑名单
            String key = TOKEN_BLACKLIST_PREFIX + token;
            // 设置过期时间为token的剩余有效期
            redisTemplate.opsForValue().set(key, "revoked", 24, TimeUnit.HOURS);
            
            // 同时删除关联的刷新token
            String username = getUsernameFromToken(token);
            if (username != null) {
                String refreshKey = REFRESH_TOKEN_PREFIX + username;
                redisTemplate.delete(refreshKey);
            }
            
            log.info("Token revoked successfully");
        } catch (Exception e) {
            log.error("Failed to revoke token", e);
        }
    }
    
    @Override
    public boolean isTokenRevoked(String token) {
        try {
            String key = TOKEN_BLACKLIST_PREFIX + token;
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("Failed to check if token is revoked", e);
            return false;
        }
    }
}