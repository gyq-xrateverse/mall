package com.macro.mall.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Portal模块专用JWT工具类
 * 专门为会员用户设计，支持用户ID、用户类型等Portal特有功能
 * @author Claude
 * @since 2025-09-10
 */
@Slf4j
@Component("portalJwtTokenUtil")
public class PortalJwtTokenUtil {
    
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_USER_ID = "userId";
    private static final String CLAIM_KEY_USER_TYPE = "userType";
    private static final String CLAIM_KEY_REGISTER_TYPE = "registerType";
    private static final String CLAIM_KEY_CREATED = "iat";
    private static final String CLAIM_KEY_EXPIRED = "exp";
    private static final String CLAIM_KEY_TOKEN_TYPE = "tokenType";
    
    private static final String USER_TYPE_MEMBER = "member";
    private static final String TOKEN_TYPE_ACCESS = "access";
    private static final String TOKEN_TYPE_REFRESH = "refresh";
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    @Value("${jwt.refresh-expiration:604800}")  // 默认7天
    private Long refreshExpiration;
    
    /**
     * 生成访问token（包含完整用户信息）
     */
    public String generateAccessToken(String username, Long userId) {
        return generateAccessToken(username, userId, null);
    }
    
    /**
     * 生成访问token（包含注册类型）
     */
    public String generateAccessToken(String username, Long userId, String registerType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USER_TYPE, USER_TYPE_MEMBER);
        claims.put(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        claims.put(CLAIM_KEY_CREATED, new Date());
        
        if (registerType != null) {
            claims.put(CLAIM_KEY_REGISTER_TYPE, registerType);
        }
        
        return generateToken(claims, expiration);
    }
    
    /**
     * 生成刷新token
     */
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_USER_ID, userId);
        claims.put(CLAIM_KEY_USER_TYPE, USER_TYPE_MEMBER);
        claims.put(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        claims.put(CLAIM_KEY_CREATED, new Date());
        
        return generateToken(claims, refreshExpiration);
    }
    
    /**
     * 根据claims生成token
     */
    private String generateToken(Map<String, Object> claims, Long expiration) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    /**
     * 从token中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.warn("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? claims.getSubject() : null;
        } catch (Exception e) {
            log.error("Failed to get username from token", e);
            return null;
        }
    }
    
    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims != null && claims.get(CLAIM_KEY_USER_ID) != null) {
                Object userIdObj = claims.get(CLAIM_KEY_USER_ID);
                if (userIdObj instanceof Integer) {
                    return ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    return (Long) userIdObj;
                }
            }
            return null;
        } catch (Exception e) {
            log.error("Failed to get user ID from token", e);
            return null;
        }
    }
    
    /**
     * 从token中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? (String) claims.get(CLAIM_KEY_USER_TYPE) : null;
        } catch (Exception e) {
            log.error("Failed to get user type from token", e);
            return null;
        }
    }
    
    /**
     * 从token中获取token类型
     */
    public String getTokenTypeFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? (String) claims.get(CLAIM_KEY_TOKEN_TYPE) : null;
        } catch (Exception e) {
            log.error("Failed to get token type from token", e);
            return null;
        }
    }
    
    /**
     * 从token中获取注册类型
     */
    public String getRegisterTypeFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? (String) claims.get(CLAIM_KEY_REGISTER_TYPE) : null;
        } catch (Exception e) {
            log.error("Failed to get register type from token", e);
            return null;
        }
    }
    
    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return false;
            }
            
            // 检查是否是会员token
            String userType = (String) claims.get(CLAIM_KEY_USER_TYPE);
            if (!USER_TYPE_MEMBER.equals(userType)) {
                return false;
            }
            
            // 检查是否过期
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
            
        } catch (Exception e) {
            log.error("Token validation failed", e);
            return false;
        }
    }
    
    /**
     * 验证访问token（非刷新token）
     */
    public boolean validateAccessToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        String tokenType = getTokenTypeFromToken(token);
        return TOKEN_TYPE_ACCESS.equals(tokenType);
    }
    
    /**
     * 验证刷新token
     */
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) {
            return false;
        }
        
        String tokenType = getTokenTypeFromToken(token);
        return TOKEN_TYPE_REFRESH.equals(tokenType);
    }
    
    /**
     * 检查token是否即将过期（剩余时间少于30分钟）
     */
    public boolean isTokenExpiringSoon(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            if (claims == null) {
                return true;
            }
            
            Date expiration = claims.getExpiration();
            if (expiration == null) {
                return true;
            }
            
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return remainingTime < 30 * 60 * 1000; // 30分钟
            
        } catch (Exception e) {
            log.error("Failed to check token expiration", e);
            return true;
        }
    }
    
    /**
     * 获取token的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims != null ? claims.getExpiration() : null;
        } catch (Exception e) {
            log.error("Failed to get expiration date from token", e);
            return null;
        }
    }
    
    /**
     * 检查token是否已过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration != null && expiration.before(new Date());
    }
}