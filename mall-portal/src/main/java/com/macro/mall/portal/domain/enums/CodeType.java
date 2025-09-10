package com.macro.mall.portal.domain.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 验证码类型枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum CodeType {
    REGISTER(1, "register", "注册"),
    LOGIN(2, "login", "登录"),
    RESET_PASSWORD(3, "reset", "重置密码");
    
    private final Integer code;
    private final String type;
    private final String description;
    
    public static CodeType fromCode(Integer code) {
        for (CodeType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid CodeType code: " + code);
    }
    
    public static CodeType fromType(String type) {
        for (CodeType codeType : values()) {
            if (codeType.getType().equals(type)) {
                return codeType;
            }
        }
        throw new IllegalArgumentException("Invalid CodeType type: " + type);
    }
}