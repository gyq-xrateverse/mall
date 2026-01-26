package com.macro.mall.common.enums;

/**
 * 积分类型枚举
 * Created by macro on 2026/01/23.
 */
public enum IntegrationCreditType {

    /**
     * 免费积分（有过期时间）
     */
    FREE(1, "免费积分"),

    /**
     * 永久积分（无过期时间）
     */
    PERMANENT(2, "永久积分");

    private final Integer code;
    private final String description;

    IntegrationCreditType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据code获取枚举
     * @param code 积分类型代码
     * @return 对应的枚举值，如果找不到则返回null
     */
    public static IntegrationCreditType getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (IntegrationCreditType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断code是否有效
     * @param code 积分类型代码
     * @return true-有效，false-无效
     */
    public static boolean isValid(Integer code) {
        return getByCode(code) != null;
    }
}
