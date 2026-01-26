package com.macro.mall.common.enums;

/**
 * 业务类型枚举
 * Created by macro on 2026/01/26.
 */
public enum BusinessType {

    /**
     * 视频生成任务
     */
    VIDEO_GENERATION("video_generation", 10, "视频生成任务"),

    /**
     * 图片生成任务
     */
    IMAGE_GENERATION("image_generation", 11, "图片生成任务"),

    /**
     * AI任务
     */
    AI_TASK("AI_TASK", 12, "AI任务"),

    /**
     * 未知类型（默认值）
     */
    UNKNOWN("unknown", 99, "未知类型");

    private final String name;
    private final Integer code;
    private final String description;

    BusinessType(String name, Integer code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据字符串名称获取枚举
     * @param name 业务类型名称
     * @return 对应的枚举值，如果找不到则返回 UNKNOWN
     */
    public static BusinessType fromName(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        for (BusinessType type : values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * 根据整数代码获取枚举
     * @param code 业务类型代码
     * @return 对应的枚举值，如果找不到则返回 UNKNOWN
     */
    public static BusinessType fromCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (BusinessType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return UNKNOWN;
    }

    /**
     * 判断名称是否有效
     * @param name 业务类型名称
     * @return true-有效，false-无效
     */
    public static boolean isValidName(String name) {
        return fromName(name) != UNKNOWN;
    }
}
