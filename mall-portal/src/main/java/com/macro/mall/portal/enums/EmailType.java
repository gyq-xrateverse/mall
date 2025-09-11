package com.macro.mall.portal.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 邮件类型枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum EmailType {
    VERIFICATION_CODE(1, "验证码邮件"),
    WELCOME(2, "欢迎邮件"),
    PASSWORD_RESET(3, "密码重置邮件"),
    SECURITY_ALERT(4, "安全提醒邮件");
    
    private final Integer code;
    private final String description;
    
    public static EmailType fromCode(Integer code) {
        for (EmailType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid EmailType code: " + code);
    }
}