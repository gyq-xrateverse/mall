package com.macro.mall.component;

import com.macro.mall.common.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存安全防护组件
 * 提供缓存操作的安全防护机制，包括频率限制、权限验证等
 */
@Component
public class CacheSecurityGuard {

    private static final Logger logger = LoggerFactory.getLogger(CacheSecurityGuard.class);

    @Autowired
    private RedisService redisService;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    // 内存中的频率限制计数器
    private final ConcurrentHashMap<String, AtomicInteger> rateLimitCounters = new ConcurrentHashMap<>();

    // 默认频率限制配置
    private static final int DEFAULT_MAX_OPERATIONS_PER_MINUTE = 60; // 每分钟最多60次操作
    private static final int DEFAULT_MAX_OPERATIONS_PER_HOUR = 1000; // 每小时最多1000次操作
    private static final long RATE_LIMIT_WINDOW_MINUTES = 1; // 频率限制窗口（分钟）
    private static final long RATE_LIMIT_WINDOW_HOURS = 1; // 频率限制窗口（小时）

    /**
     * 检查操作频率是否允许
     * @param operatorId 操作人员ID
     * @param operation 操作类型
     * @return 是否允许操作
     */
    public boolean checkRateLimit(String operatorId, String operation) {
        try {
            String minuteKey = buildRateLimitKey(operatorId, operation, "minute");
            String hourKey = buildRateLimitKey(operatorId, operation, "hour");

            // 检查分钟级别限制
            int minuteCount = getOperationCount(minuteKey);
            if (minuteCount >= DEFAULT_MAX_OPERATIONS_PER_MINUTE) {
                logger.warn("操作频率超限：用户 {} 的 {} 操作在1分钟内已达到 {} 次", operatorId, operation, minuteCount);
                return false;
            }

            // 检查小时级别限制
            int hourCount = getOperationCount(hourKey);
            if (hourCount >= DEFAULT_MAX_OPERATIONS_PER_HOUR) {
                logger.warn("操作频率超限：用户 {} 的 {} 操作在1小时内已达到 {} 次", operatorId, operation, hourCount);
                return false;
            }

            // 增加计数
            incrementOperationCount(minuteKey, 60); // 1分钟过期
            incrementOperationCount(hourKey, 3600); // 1小时过期

            return true;

        } catch (Exception e) {
            logger.error("检查操作频率失败：操作人 {}, 操作 {}, 错误: {}", operatorId, operation, e.getMessage(), e);
            // 出现异常时采用保守策略，允许操作但记录错误
            return true;
        }
    }

    /**
     * 验证操作权限
     * @param operatorId 操作人员ID
     * @param operation 操作类型
     * @return 是否有权限
     */
    public boolean checkPermission(String operatorId, String operation) {
        try {
            // 基础权限验证 - 这里可以扩展为从数据库或其他权限系统查询
            if (operatorId == null || operatorId.trim().isEmpty()) {
                logger.warn("操作权限验证失败：操作人员ID为空，操作: {}", operation);
                return false;
            }

            // 可以在这里添加更复杂的权限验证逻辑
            // 例如：检查用户角色、检查操作权限映射等

            logger.debug("操作权限验证通过：操作人 {}, 操作 {}", operatorId, operation);
            return true;

        } catch (Exception e) {
            logger.error("操作权限验证失败：操作人 {}, 操作 {}, 错误: {}", operatorId, operation, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证消息来源
     * @param message 消息内容
     * @return 是否为合法来源
     */
    public boolean validateMessageSource(String message) {
        try {
            // 基础消息格式验证
            if (message == null || message.trim().isEmpty()) {
                logger.warn("消息来源验证失败：消息内容为空");
                return false;
            }

            // 检查消息格式是否符合预期
            if (!message.contains("action") || !message.contains("resourceType") || !message.contains("timestamp")) {
                logger.warn("消息来源验证失败：消息格式不符合预期");
                return false;
            }

            // 可以在这里添加更复杂的消息来源验证
            // 例如：数字签名验证、消息哈希验证等

            return true;

        } catch (Exception e) {
            logger.error("消息来源验证失败：消息 {}, 错误: {}", message, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 记录安全事件
     * @param operatorId 操作人员ID
     * @param operation 操作类型
     * @param eventType 事件类型
     * @param description 事件描述
     */
    public void logSecurityEvent(String operatorId, String operation, String eventType, String description) {
        try {
            String logMessage = String.format(
                "安全事件 - 类型: %s, 操作人: %s, 操作: %s, 描述: %s, 时间: %d",
                eventType, operatorId, operation, description, System.currentTimeMillis()
            );

            logger.warn(logMessage);

            // 可以将安全事件存储到专门的安全日志系统
            // 例如：发送到安全监控系统、存储到安全数据库等

        } catch (Exception e) {
            logger.error("记录安全事件失败：{}", e.getMessage(), e);
        }
    }

    /**
     * 构建频率限制键
     */
    private String buildRateLimitKey(String operatorId, String operation, String timeWindow) {
        long currentWindow;
        if ("minute".equals(timeWindow)) {
            currentWindow = System.currentTimeMillis() / (60 * 1000); // 分钟窗口
        } else {
            currentWindow = System.currentTimeMillis() / (60 * 60 * 1000); // 小时窗口
        }
        return String.format("%s:rate_limit:%s:%s:%s:%d", REDIS_DATABASE, operatorId, operation, timeWindow, currentWindow);
    }

    /**
     * 获取操作计数
     */
    private int getOperationCount(String key) {
        try {
            Object count = redisService.get(key);
            return count != null ? Integer.parseInt(count.toString()) : 0;
        } catch (Exception e) {
            logger.error("获取操作计数失败：键 {}, 错误: {}", key, e.getMessage());
            return 0;
        }
    }

    /**
     * 增加操作计数
     */
    private void incrementOperationCount(String key, long expireSeconds) {
        try {
            Object currentCount = redisService.get(key);
            int newCount = currentCount != null ? Integer.parseInt(currentCount.toString()) + 1 : 1;
            redisService.set(key, newCount, expireSeconds);
        } catch (Exception e) {
            logger.error("增加操作计数失败：键 {}, 错误: {}", key, e.getMessage());
        }
    }
}