package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * 缓存监控结果
 */
public class CacheMonitorResult {

    @Schema(title = "管理端缓存统计")
    private Map<String, Integer> adminCacheStats;

    @Schema(title = "门户端缓存统计")
    private Map<String, Integer> portalCacheStats;

    @Schema(title = "频率限制统计")
    private Map<String, Integer> rateLimitStats;

    @Schema(title = "缓存命中率统计")
    private Map<String, Double> hitRateStats;

    @Schema(title = "统计时间戳")
    private Long timestamp;

    @Schema(title = "监控状态")
    private String status;

    @Schema(title = "监控消息")
    private String message;

    public Map<String, Integer> getAdminCacheStats() {
        return adminCacheStats;
    }

    public void setAdminCacheStats(Map<String, Integer> adminCacheStats) {
        this.adminCacheStats = adminCacheStats;
    }

    public Map<String, Integer> getPortalCacheStats() {
        return portalCacheStats;
    }

    public void setPortalCacheStats(Map<String, Integer> portalCacheStats) {
        this.portalCacheStats = portalCacheStats;
    }

    public Map<String, Integer> getRateLimitStats() {
        return rateLimitStats;
    }

    public void setRateLimitStats(Map<String, Integer> rateLimitStats) {
        this.rateLimitStats = rateLimitStats;
    }

    public Map<String, Double> getHitRateStats() {
        return hitRateStats;
    }

    public void setHitRateStats(Map<String, Double> hitRateStats) {
        this.hitRateStats = hitRateStats;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CacheMonitorResult{" +
                "adminCacheStats=" + adminCacheStats +
                ", portalCacheStats=" + portalCacheStats +
                ", rateLimitStats=" + rateLimitStats +
                ", hitRateStats=" + hitRateStats +
                ", timestamp=" + timestamp +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}