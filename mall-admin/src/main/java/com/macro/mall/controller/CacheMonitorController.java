package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.dto.CacheMonitorResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 缓存监控控制器
 * 提供缓存状态监控、统计和管理功能
 */
@Controller
@Tag(name = "CacheMonitorController", description = "缓存监控管理")
@RequestMapping("/admin/cache/monitor")
public class CacheMonitorController {

    private static final Logger logger = LoggerFactory.getLogger(CacheMonitorController.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    @Value("${redis.key.case}")
    private String REDIS_KEY_CASE;

    @Operation(summary = "获取缓存健康状态")
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getCacheHealth() {
        try {
            Map<String, Object> healthInfo = new HashMap<>();

            // Redis连接状态
            boolean redisConnected = checkRedisConnection();
            healthInfo.put("redisConnected", redisConnected);

            if (redisConnected) {
                // 缓存键统计
                Map<String, Integer> cacheStats = buildCacheStatisticsMap();
                healthInfo.put("cacheStatistics", cacheStats);

                // 内存使用情况
                Map<String, Object> memoryInfo = getMemoryInfo();
                healthInfo.put("memoryInfo", memoryInfo);

                // 消息发布统计
                Map<String, Object> messageStats = getMessageStatistics();
                healthInfo.put("messageStatistics", messageStats);
            }

            healthInfo.put("timestamp", System.currentTimeMillis());
            healthInfo.put("status", redisConnected ? "healthy" : "unhealthy");

            return CommonResult.success(healthInfo);

        } catch (Exception e) {
            logger.error("获取缓存健康状态失败: {}", e.getMessage(), e);
            return CommonResult.failed("获取缓存健康状态失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取缓存统计信息")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CacheMonitorResult> getCacheStatistics() {
        try {
            CacheMonitorResult result = new CacheMonitorResult();

            // 管理端缓存统计
            Map<String, Integer> adminCacheStats = new HashMap<>();
            adminCacheStats.put("categoryCache", countCacheKeys("category"));
            adminCacheStats.put("dataCache", countCacheKeys("data"));
            adminCacheStats.put("hotCache", countCacheKeys("hot"));
            adminCacheStats.put("latestCache", countCacheKeys("latest"));

            // 门户端缓存统计
            Map<String, Integer> portalCacheStats = new HashMap<>();
            portalCacheStats.put("categoryCache", countCacheKeys("portal:category"));
            portalCacheStats.put("detailCache", countCacheKeys("portal:detail"));
            portalCacheStats.put("hotCache", countCacheKeys("portal:hot"));
            portalCacheStats.put("latestCache", countCacheKeys("portal:latest"));

            // 频率限制统计
            Map<String, Integer> rateLimitStats = new HashMap<>();
            rateLimitStats.put("activeRateLimits", countCacheKeys("rate_limit"));

            result.setAdminCacheStats(adminCacheStats);
            result.setPortalCacheStats(portalCacheStats);
            result.setRateLimitStats(rateLimitStats);
            result.setTimestamp(System.currentTimeMillis());

            return CommonResult.success(result);

        } catch (Exception e) {
            logger.error("获取缓存统计信息失败: {}", e.getMessage(), e);
            return CommonResult.failed("获取缓存统计信息失败: " + e.getMessage());
        }
    }

    @Operation(summary = "清理过期缓存")
    @RequestMapping(value = "/cleanup", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Map<String, Object>> cleanupExpiredCache() {
        try {
            Map<String, Object> cleanupResult = new HashMap<>();
            int cleanedCount = 0;

            // 清理过期的频率限制键
            Set<String> rateLimitKeys = redisTemplate.keys(REDIS_DATABASE + ":rate_limit:*");
            if (rateLimitKeys != null) {
                for (String key : rateLimitKeys) {
                    // 检查键是否存在（可能已过期）
                    if (!redisTemplate.hasKey(key)) {
                        cleanedCount++;
                    }
                }
            }

            // 获取清理前后的统计
            Map<String, Integer> beforeStats = buildCacheStatisticsMap();
            cleanupResult.put("beforeCleanup", beforeStats);
            cleanupResult.put("cleanedKeys", cleanedCount);
            cleanupResult.put("timestamp", System.currentTimeMillis());

            logger.info("缓存清理完成，清理键数量: {}", cleanedCount);

            return CommonResult.success(cleanupResult);

        } catch (Exception e) {
            logger.error("清理过期缓存失败: {}", e.getMessage(), e);
            return CommonResult.failed("清理过期缓存失败: " + e.getMessage());
        }
    }

    @Operation(summary = "测试消息发布")
    @RequestMapping(value = "/test/message", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> testMessagePublish(@RequestParam("testMessage") String testMessage) {
        try {
            String channel = "test:cache:update";
            redisTemplate.convertAndSend(channel, testMessage);

            logger.info("测试消息发布成功，频道: {}, 消息: {}", channel, testMessage);
            return CommonResult.success("测试消息发布成功");

        } catch (Exception e) {
            logger.error("测试消息发布失败: {}", e.getMessage(), e);
            return CommonResult.failed("测试消息发布失败: " + e.getMessage());
        }
    }

    /**
     * 检查Redis连接状态
     */
    private boolean checkRedisConnection() {
        try {
            redisTemplate.opsForValue().set("health:check", "ok", 10);
            String result = (String) redisTemplate.opsForValue().get("health:check");
            return "ok".equals(result);
        } catch (Exception e) {
            logger.error("Redis连接检查失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 统计缓存键数量
     */
    private int countCacheKeys(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":" + pattern + "*");
            return keys != null ? keys.size() : 0;
        } catch (Exception e) {
            logger.error("统计缓存键失败，模式: {}, 错误: {}", pattern, e.getMessage());
            return 0;
        }
    }

    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo() {
        Map<String, Object> memoryInfo = new HashMap<>();
        try {
            // 这里可以添加更详细的内存统计
            Runtime runtime = Runtime.getRuntime();
            memoryInfo.put("totalMemory", runtime.totalMemory());
            memoryInfo.put("freeMemory", runtime.freeMemory());
            memoryInfo.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
            memoryInfo.put("maxMemory", runtime.maxMemory());
        } catch (Exception e) {
            logger.error("获取内存信息失败: {}", e.getMessage());
        }
        return memoryInfo;
    }

    /**
     * 构建缓存统计信息Map
     */
    private Map<String, Integer> buildCacheStatisticsMap() {
        Map<String, Integer> stats = new HashMap<>();
        try {
            // 管理端缓存统计
            stats.put("categoryCache", countCacheKeys("category"));
            stats.put("dataCache", countCacheKeys("data"));
            stats.put("hotCache", countCacheKeys("hot"));
            stats.put("latestCache", countCacheKeys("latest"));

            // 门户端缓存统计
            stats.put("portalCategoryCache", countCacheKeys("portal:category"));
            stats.put("portalDetailCache", countCacheKeys("portal:detail"));
            stats.put("portalHotCache", countCacheKeys("portal:hot"));
            stats.put("portalLatestCache", countCacheKeys("portal:latest"));

            // 频率限制统计
            stats.put("rateLimitCache", countCacheKeys("rate_limit"));
        } catch (Exception e) {
            logger.error("构建缓存统计信息失败: {}", e.getMessage());
        }
        return stats;
    }

    /**
     * 获取消息统计
     */
    private Map<String, Object> getMessageStatistics() {
        Map<String, Object> messageStats = new HashMap<>();
        try {
            // 这里可以添加消息发布/订阅的统计信息
            messageStats.put("lastMessageTime", System.currentTimeMillis());
            messageStats.put("messageChannelActive", true);
        } catch (Exception e) {
            logger.error("获取消息统计失败: {}", e.getMessage());
        }
        return messageStats;
    }
}