package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.portal.service.TokenService;
import com.macro.mall.security.util.PortalJwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Portal模块Token服务实现类
 * 使用专用的PortalJwtTokenUtil，支持完整的JWT功能
 * @author Claude
 * @since 2025-09-10
 */
@Slf4j
@Service
public class TokenServiceImpl implements TokenService {
    
    @Autowired
    @Qualifier("portalJwtTokenUtil")
    private PortalJwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    private static final String TOKEN_BLACKLIST_PREFIX = "portal:token_blacklist:";
    private static final String REFRESH_TOKEN_PREFIX = "portal:refresh_token:";
    private static final String ACCESS_TOKEN_PREFIX = "portal:access_token:";
    
    @Override
    public String generateAccessToken(String username, Long userId) {
        String accessToken = jwtTokenUtil.generateAccessToken(username, userId);

        // 将access token存储到Redis中，过期时间与JWT token一致
        String key = ACCESS_TOKEN_PREFIX + username + ":" + userId;
        redisTemplate.opsForValue().set(key, accessToken, 24, TimeUnit.HOURS);

        return accessToken;
    }
    
    @Override
    public String generateRefreshToken(String username) {
        // 需要先获取用户ID
        UmsMember member = findMemberByUsername(username);
        if (member == null) {
            log.warn("User not found for username: {}", username);
            return null;
        }
        
        String refreshToken = jwtTokenUtil.generateRefreshToken(username, member.getId());
        
        // 将刷新token存储到Redis中，设置过期时间
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
        
        return refreshToken;
    }
    
    @Override
    public Map<String, String> generateTokenPair(String username, Long userId) {
        Map<String, String> tokenPair = new HashMap<>();
        tokenPair.put("access_token", generateAccessToken(username, userId));
        tokenPair.put("refresh_token", generateRefreshTokenWithUserId(username, userId));
        tokenPair.put("token_type", "Bearer");
        tokenPair.put("expires_in", "86400"); // 24小时
        return tokenPair;
    }
    
    /**
     * 使用已知的userId生成刷新token（避免重复查询数据库）
     */
    private String generateRefreshTokenWithUserId(String username, Long userId) {
        String refreshToken = jwtTokenUtil.generateRefreshToken(username, userId);
        
        // 将刷新token存储到Redis中
        String key = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(key, refreshToken, 7, TimeUnit.DAYS);
        
        return refreshToken;
    }
    
    @Override
    public String refreshAccessToken(String refreshToken) {
        try {
            // 验证刷新token
            if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
                log.warn("Invalid refresh token");
                return null;
            }

            // 从刷新token中提取用户信息
            String username = jwtTokenUtil.getUsernameFromToken(refreshToken);
            Long userId = jwtTokenUtil.getUserIdFromToken(refreshToken);

            if (username == null || userId == null) {
                log.warn("Invalid refresh token: missing user info");
                return null;
            }

            // 检查刷新token是否存在于Redis中
            String key = REFRESH_TOKEN_PREFIX + username;
            String storedRefreshToken = redisTemplate.opsForValue().get(key);
            if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
                log.warn("Refresh token not found or mismatched for user: {}", username);
                return null;
            }

            // 生成新的访问token（会自动存储到Redis）
            return generateAccessToken(username, userId);

        } catch (Exception e) {
            log.error("Token刷新失败: Redis操作异常", e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            // 检查token是否已被注销
            if (isTokenRevoked(token)) {
                return false;
            }

            // 先检查Redis中是否存在该token
            if (!isTokenExistsInRedis(token)) {
                log.warn("Token not found in Redis, treating as invalid");
                return false;
            }

            // 使用专用的验证方法
            return jwtTokenUtil.validateAccessToken(token);

        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
    
    @Override
    public String getUsernameFromToken(String token) {
        try {
            return jwtTokenUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.error("Failed to get username from token", e);
            return null;
        }
    }
    
    @Override
    public Long getUserIdFromToken(String token) {
        try {
            // 直接从JWT claims中获取userId，无需Redis查询
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

            // 从Redis中删除access token
            String username = getUsernameFromToken(token);
            Long userId = getUserIdFromToken(token);
            if (username != null && userId != null) {
                String accessTokenKey = ACCESS_TOKEN_PREFIX + username + ":" + userId;
                redisTemplate.delete(accessTokenKey);

                // 同时删除关联的刷新token
                String refreshKey = REFRESH_TOKEN_PREFIX + username;
                redisTemplate.delete(refreshKey);
            }

            log.info("Token revoked successfully");
        } catch (Exception e) {
            log.error("Token注销失败: Redis操作异常", e);
            throw new RuntimeException("Redis操作异常", e);
        }
    }

    @Override
    public void revokeTokenWithUserInfo(String token, String username, Long userId) {
        try {
            // 将token添加到黑名单
            String key = TOKEN_BLACKLIST_PREFIX + token;
            // 设置过期时间为token的剩余有效期
            redisTemplate.opsForValue().set(key, "revoked", 24, TimeUnit.HOURS);

            // 从Redis中删除access token（使用传入的用户信息，避免重复解析token）
            if (username != null && userId != null) {
                String accessTokenKey = ACCESS_TOKEN_PREFIX + username + ":" + userId;
                redisTemplate.delete(accessTokenKey);

                // 同时删除关联的刷新token
                String refreshKey = REFRESH_TOKEN_PREFIX + username;
                redisTemplate.delete(refreshKey);
            }

            log.info("Token revoked successfully with user info: username={}, userId={}", username, userId);
        } catch (Exception e) {
            log.error("Token注销失败: Redis操作异常", e);
            throw new RuntimeException("Redis操作异常", e);
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
    
    /**
     * 检查token是否即将过期
     */
    public boolean isTokenExpiringSoon(String token) {
        return jwtTokenUtil.isTokenExpiringSoon(token);
    }
    
    /**
     * 获取token中的用户类型
     */
    public String getUserTypeFromToken(String token) {
        return jwtTokenUtil.getUserTypeFromToken(token);
    }
    
    /**
     * 获取token中的注册类型
     */
    public String getRegisterTypeFromToken(String token) {
        return jwtTokenUtil.getRegisterTypeFromToken(token);
    }
    
    /**
     * 检查token是否在Redis中存在
     */
    private boolean isTokenExistsInRedis(String token) {
        try {
            String username = jwtTokenUtil.getUsernameFromToken(token);
            Long userId = jwtTokenUtil.getUserIdFromToken(token);
            if (username != null && userId != null) {
                String key = ACCESS_TOKEN_PREFIX + username + ":" + userId;
                String storedToken = redisTemplate.opsForValue().get(key);
                return storedToken != null && storedToken.equals(token);
            }
            return false;
        } catch (Exception e) {
            log.error("Redis连接异常: {}", e.getMessage());
            throw new RuntimeException("Redis connection failed", e);
        }
    }

    /**
     * 根据用户名查找用户
     */
    private UmsMember findMemberByUsername(String username) {
        try {
            UmsMemberExample example = new UmsMemberExample();
            example.createCriteria().andUsernameEqualTo(username);
            List<UmsMember> members = memberMapper.selectByExample(example);
            return members.isEmpty() ? null : members.get(0);
        } catch (Exception e) {
            log.warn("Failed to find member by username: {}", username, e);
            return null;
        }
    }
}