package com.macro.mall.dto;

import java.util.List;

/**
 * 缓存更新消息格式
 * 用于在管理端和门户端之间传递缓存更新信息
 */
public class CacheUpdateMessage {

    /**
     * 操作类型枚举
     */
    public enum ActionType {
        CREATE("CREATE"),
        UPDATE("UPDATE"),
        DELETE("DELETE"),
        BATCH_DELETE("BATCH_DELETE"),
        STATUS_UPDATE("STATUS_UPDATE");

        private final String value;

        ActionType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 资源类型枚举
     */
    public enum ResourceType {
        CASE("CASE"),
        CATEGORY("CATEGORY");

        private final String value;

        ResourceType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 操作类型
     */
    private ActionType action;

    /**
     * 资源类型
     */
    private ResourceType resourceType;

    /**
     * 资源ID
     */
    private String resourceId;

    /**
     * 需要清理的缓存键列表
     */
    private List<String> cacheKeys;

    /**
     * 操作时间戳
     */
    private Long timestamp;

    /**
     * 操作用户ID
     */
    private String operator;

    public CacheUpdateMessage() {
    }

    public CacheUpdateMessage(ActionType action, ResourceType resourceType, String resourceId, List<String> cacheKeys, String operator) {
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.cacheKeys = cacheKeys;
        this.operator = operator;
        this.timestamp = System.currentTimeMillis();
    }

    public ActionType getAction() {
        return action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<String> getCacheKeys() {
        return cacheKeys;
    }

    public void setCacheKeys(List<String> cacheKeys) {
        this.cacheKeys = cacheKeys;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return "CacheUpdateMessage{" +
                "action=" + action +
                ", resourceType=" + resourceType +
                ", resourceId='" + resourceId + '\'' +
                ", cacheKeys=" + cacheKeys +
                ", timestamp=" + timestamp +
                ", operator='" + operator + '\'' +
                '}';
    }
}