package com.macro.mall.component;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.service.CaseCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 缓存故障恢复机制
 * 提供Redis连接故障时的降级策略和自动恢复功能
 */
@Component
public class CacheFailureRecovery {

    private static final Logger logger = LoggerFactory.getLogger(CacheFailureRecovery.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private CaseCacheService caseCacheService;

    @Autowired
    private CacheSecurityGuard cacheSecurityGuard;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private volatile boolean redisAvailable = true;
    private volatile long lastHealthCheckTime = System.currentTimeMillis();

    @PostConstruct
    public void init() {
        // 启动定期健康检查
        startHealthCheck();

        // 启动故障恢复检查
        startRecoveryCheck();

        logger.info("缓存故障恢复机制初始化完成");
    }

    /**
     * 启动Redis健康检查
     */
    private void startHealthCheck() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                checkRedisHealth();
            } catch (Exception e) {
                logger.error("Redis健康检查异常: {}", e.getMessage(), e);
            }
        }, 30, 30, TimeUnit.SECONDS); // 每30秒检查一次
    }

    /**
     * 启动故障恢复检查
     */
    private void startRecoveryCheck() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                if (!redisAvailable) {
                    attemptRecovery();
                }
            } catch (Exception e) {
                logger.error("故障恢复检查异常: {}", e.getMessage(), e);
            }
        }, 60, 60, TimeUnit.SECONDS); // 每60秒尝试恢复一次
    }

    /**
     * 检查Redis健康状态
     */
    private void checkRedisHealth() {
        try {
            String testKey = "health:check:" + System.currentTimeMillis();
            redisTemplate.opsForValue().set(testKey, "ok", 10, TimeUnit.SECONDS);
            String result = (String) redisTemplate.opsForValue().get(testKey);

            boolean currentStatus = "ok".equals(result);

            if (currentStatus != redisAvailable) {
                if (currentStatus) {
                    logger.info("Redis连接已恢复");
                    onRedisRecovered();
                } else {
                    logger.warn("Redis连接丢失");
                    onRedisFailure();
                }
                redisAvailable = currentStatus;
            }

            lastHealthCheckTime = System.currentTimeMillis();

        } catch (Exception e) {
            if (redisAvailable) {
                logger.error("Redis健康检查失败，标记为不可用: {}", e.getMessage());
                redisAvailable = false;
                onRedisFailure();
            }
        }
    }

    /**
     * Redis故障时的处理
     */
    private void onRedisFailure() {
        try {
            logger.warn("Redis故障检测，启动降级模式");

            // 记录故障事件
            cacheSecurityGuard.logSecurityEvent("system", "REDIS_FAILURE", "SYSTEM_EVENT",
                "Redis连接失败，启动降级模式");

            // 可以在这里实现降级策略
            // 例如：切换到本地缓存、记录操作日志等

        } catch (Exception e) {
            logger.error("Redis故障处理异常: {}", e.getMessage(), e);
        }
    }

    /**
     * Redis恢复时的处理
     */
    private void onRedisRecovered() {
        try {
            logger.info("Redis连接恢复，退出降级模式");

            // 记录恢复事件
            cacheSecurityGuard.logSecurityEvent("system", "REDIS_RECOVERY", "SYSTEM_EVENT",
                "Redis连接恢复正常");

            // 可以在这里实现恢复后的重建逻辑
            // 例如：重建关键缓存、同步数据等

        } catch (Exception e) {
            logger.error("Redis恢复处理异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 尝试故障恢复
     */
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public void attemptRecovery() {
        try {
            logger.info("尝试Redis故障恢复...");

            // 简单的连接测试
            redisTemplate.opsForValue().set("recovery:test", "test", 5, TimeUnit.SECONDS);

            logger.info("Redis故障恢复成功");
            redisAvailable = true;
            onRedisRecovered();

        } catch (Exception e) {
            logger.warn("Redis故障恢复失败: {}", e.getMessage());
            throw e; // 重新抛出异常以触发重试
        }
    }

    /**
     * 安全的缓存操作，带有故障处理
     */
    public boolean safeCacheOperation(Runnable operation, String operationName) {
        try {
            if (!redisAvailable) {
                logger.warn("Redis不可用，跳过缓存操作: {}", operationName);
                return false;
            }

            operation.run();
            return true;

        } catch (Exception e) {
            logger.error("缓存操作失败: {}, 错误: {}", operationName, e.getMessage());

            // 检查是否是连接问题
            if (isConnectionError(e)) {
                redisAvailable = false;
                onRedisFailure();
            }

            return false;
        }
    }

    /**
     * 安全的缓存消息发布
     */
    public boolean safeMessagePublish(String channel, Object message) {
        return safeCacheOperation(() -> {
            redisTemplate.convertAndSend(channel, message);
        }, "消息发布到频道: " + channel);
    }

    /**
     * 强制清理所有缓存（紧急情况使用）
     */
    public void emergencyCacheClear(String operator) {
        try {
            logger.warn("执行紧急缓存清理，操作人: {}", operator);

            caseCacheService.batchClearCaseListCache(operator);

            cacheSecurityGuard.logSecurityEvent(operator, "EMERGENCY_CLEAR", "ADMIN_OPERATION",
                "执行紧急缓存清理");

            logger.info("紧急缓存清理完成");

        } catch (Exception e) {
            logger.error("紧急缓存清理失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 检查是否为连接错误
     */
    private boolean isConnectionError(Exception e) {
        String message = e.getMessage();
        if (message == null) return false;

        return message.contains("Connection") ||
               message.contains("connection") ||
               message.contains("timeout") ||
               message.contains("Unable to connect");
    }

    /**
     * 获取系统状态
     */
    public boolean isRedisAvailable() {
        return redisAvailable;
    }

    /**
     * 获取最后健康检查时间
     */
    public long getLastHealthCheckTime() {
        return lastHealthCheckTime;
    }

    /**
     * 停止故障恢复服务
     */
    public void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            logger.info("缓存故障恢复服务已停止");
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
