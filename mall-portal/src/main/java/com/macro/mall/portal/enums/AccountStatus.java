package com.macro.mall.portal.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 账户状态枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum AccountStatus {
    NORMAL(1, "正常"),
    FROZEN(2, "冻结"),
    DISABLED(3, "禁用");
    
    private final Integer code;
    private final String description;
    
    public static AccountStatus fromCode(Integer code) {
        for (AccountStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid AccountStatus code: " + code);
    }
}