package com.macro.mall.portal.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 注册方式枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum RegisterType {
    EMAIL(1, "email", "邮箱注册"),
    WECHAT(2, "wechat", "微信注册"),
    GOOGLE(3, "google", "谷歌注册");
    
    private final Integer code;
    private final String type;
    private final String description;
    
    public static RegisterType fromCode(Integer code) {
        for (RegisterType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid RegisterType code: " + code);
    }
    
    public static RegisterType fromType(String type) {
        for (RegisterType registerType : values()) {
            if (registerType.getType().equals(type)) {
                return registerType;
            }
        }
        throw new IllegalArgumentException("Invalid RegisterType type: " + type);
    }
}