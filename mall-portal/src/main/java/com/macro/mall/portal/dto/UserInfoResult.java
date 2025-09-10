package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户信息结果
 * @author Claude
 * @since 2025-09-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息结果")
public class UserInfoResult {
    
    @Schema(description = "用户ID", example = "1")
    private Long id;
    
    @Schema(description = "用户名", example = "testuser")
    private String username;
    
    @Schema(description = "邮箱地址", example = "user@example.com")
    private String email;
    
    @Schema(description = "昵称", example = "测试用户")
    private String nickname;
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    
    @Schema(description = "注册方式：1-邮箱，2-微信，3-谷歌", example = "1")
    private Integer registerType;
    
    @Schema(description = "邮箱验证状态：0-未验证，1-已验证", example = "1")
    private Integer emailVerified;
    
    @Schema(description = "账户状态：1-正常，2-冻结，3-禁用", example = "1")
    private Integer accountStatus;
    
    @Schema(description = "创建时间")
    private Date createTime;
    
    @Schema(description = "最后登录时间")
    private Date lastLoginTime;
}