package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证Token结果
 * @author Claude
 * @since 2025-09-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "认证Token结果")
public class AuthTokenResult {
    
    @Schema(description = "访问token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;
    
    @Schema(description = "刷新token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;
    
    @Schema(description = "token类型", example = "Bearer")
    private String tokenType;
    
    @Schema(description = "过期时间（秒）", example = "86400")
    private Long expiresIn;
    
    @Schema(description = "用户信息")
    private UserInfoResult userInfo;
}