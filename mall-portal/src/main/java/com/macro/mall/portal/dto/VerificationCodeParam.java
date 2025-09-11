package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.*;

/**
 * 验证码请求参数
 * @author Claude
 * @since 2025-09-10
 */
@Data
@Schema(description = "验证码请求参数")
public class VerificationCodeParam {
    
    @Schema(description = "邮箱地址", required = true, example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "验证码类型：1-注册，2-登录，3-重置密码", required = true, example = "1")
    @NotNull(message = "验证码类型不能为空")
    @Min(value = 1, message = "验证码类型无效")
    @Max(value = 3, message = "验证码类型无效")
    private Integer codeType;
}