package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;

/**
 * 用户注册参数
 * @author Claude
 * @since 2025-09-10
 */
@Data
@Schema(description = "用户注册参数")
public class AuthRegisterParam {
    
    @Schema(description = "用户名", required = true, example = "testuser")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 32, message = "用户名长度必须在2-32位之间")
    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$", message = "用户名只能包含字母、数字、下划线和中文")
    private String username;
    
    @Schema(description = "邮箱地址", required = true, example = "user@example.com")
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 32, message = "密码长度必须在6-32位之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,32}$", 
             message = "密码必须包含大小写字母和数字")
    private String password;
    
    @Schema(description = "确认密码", required = true)
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
    
    @Schema(description = "验证码", required = true, example = "123456")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码必须是6位")
    private String verificationCode;
    
    @Schema(description = "注册类型", example = "1")
    @NotNull(message = "注册类型不能为空")
    @Min(value = 1, message = "注册类型无效")
    @Max(value = 3, message = "注册类型无效")
    private Integer registerType = 1; // 默认邮箱注册
}