package com.macro.mall.common.enums;

/**
 * 积分来源类型枚举
 * Created on 2026-01-26.
 */
public enum IntegrationSourceType {
    SHOPPING(0, "购物"),
    ADMIN_MODIFY(1, "管理员修改"),
    GIFT(5, "赠送"),
    ORDER_PAYMENT(7, "订单支付"),
    FREEZE(9, "冻结"),
    RELEASE(10, "释放"),
    VIDEO_GENERATION(11, "视频生成"),
    DEDUCT(12, "扣减");

    private final Integer code;
    private final String description;

    IntegrationSourceType(Integer code, String description) {
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
     */
    public static IntegrationSourceType fromCode(Integer code) {
        for (IntegrationSourceType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}
