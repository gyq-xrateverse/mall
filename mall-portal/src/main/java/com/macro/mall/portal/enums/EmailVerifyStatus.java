package com.macro.mall.portal.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 邮箱验证状态枚举
 * @author Claude
 * @since 2025-09-10
 */
@Getter
@AllArgsConstructor
public enum EmailVerifyStatus {
    UNVERIFIED(0, "未验证"),
    VERIFIED(1, "已验证");
    
    private final Integer code;
    private final String description;
    
    public static EmailVerifyStatus fromCode(Integer code) {
        for (EmailVerifyStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid EmailVerifyStatus code: " + code);
    }
}