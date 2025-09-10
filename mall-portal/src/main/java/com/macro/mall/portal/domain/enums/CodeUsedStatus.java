package com.macro.mall.portal.domain.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 验证码使用状态枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum CodeUsedStatus {
    UNUSED(0, "未使用"),
    USED(1, "已使用");
    
    private final Integer code;
    private final String description;
    
    public static CodeUsedStatus fromCode(Integer code) {
        for (CodeUsedStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid CodeUsedStatus code: " + code);
    }
}