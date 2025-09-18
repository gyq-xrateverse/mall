package com.macro.mall.portal.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.constant.CacheKeyConstants;
import com.macro.mall.portal.dto.CacheUpdateMessage;
import com.macro.mall.portal.service.PortalCaseCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * 缓存更新监听器
 * 监听管理端发送的缓存更新消息，自动清理门户端相关缓存
 */
@Component
public class CacheUpdateListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(CacheUpdateListener.class);

    @Autowired
    private PortalCaseCacheService portalCaseCacheService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String messageBody = new String(message.getBody());
            String channel = new String(message.getChannel());

            logger.info("收到缓存更新消息，频道: {}, 消息长度: {}", channel, messageBody.length());

            // 验证消息频道
            if (!CacheKeyConstants.CACHE_UPDATE_CHANNEL.equals(channel)) {
                logger.warn("未知的消息频道: {}, 预期频道: {}", channel, CacheKeyConstants.CACHE_UPDATE_CHANNEL);
                return;
            }

            // 基础消息安全验证
            if (messageBody == null || messageBody.trim().isEmpty()) {
                logger.warn("收到空消息，忽略处理");
                return;
            }

            if (messageBody.length() > 10000) { // 限制消息大小
                logger.warn("消息过大，可能存在安全风险，消息长度: {}", messageBody.length());
                return;
            }

            // 解析消息
            CacheUpdateMessage cacheUpdateMessage = objectMapper.readValue(messageBody, CacheUpdateMessage.class);

            // 验证消息时效性（5分钟内的消息才处理）
            if (cacheUpdateMessage.getTimestamp() != null) {
                long messageAge = System.currentTimeMillis() - cacheUpdateMessage.getTimestamp();
                if (messageAge > 5 * 60 * 1000) { // 5分钟
                    logger.warn("消息过期，消息时间戳: {}, 当前时间: {}, 年龄: {}ms",
                        cacheUpdateMessage.getTimestamp(), System.currentTimeMillis(), messageAge);
                    return;
                }
            }

            // 处理消息
            handleCacheUpdateMessage(cacheUpdateMessage);

        } catch (Exception e) {
            logger.error("处理缓存更新消息失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 处理缓存更新消息
     */
    private void handleCacheUpdateMessage(CacheUpdateMessage message) {
        try {
            logger.info("开始处理缓存更新消息: {}", message);

            if (message.getResourceType() != CacheUpdateMessage.ResourceType.CASE) {
                logger.info("忽略非案例类型的消息: {}", message.getResourceType());
                return;
            }

            switch (message.getAction()) {
                case CREATE:
                    handleCaseCreate(message);
                    break;
                case UPDATE:
                    handleCaseUpdate(message);
                    break;
                case DELETE:
                    handleCaseDelete(message);
                    break;
                case BATCH_DELETE:
                    handleCaseBatchDelete(message);
                    break;
                case STATUS_UPDATE:
                    handleCaseStatusUpdate(message);
                    break;
                default:
                    logger.warn("未知的操作类型: {}", message.getAction());
            }

            logger.info("缓存更新消息处理完成: {}", message.getResourceId());

        } catch (Exception e) {
            logger.error("处理缓存更新消息失败: {}, 错误: {}", message, e.getMessage(), e);
        }
    }

    /**
     * 处理案例创建
     */
    private void handleCaseCreate(CacheUpdateMessage message) {
        logger.info("处理案例创建事件，案例ID: {}", message.getResourceId());

        // 清理分类列表缓存
        portalCaseCacheService.delCaseCategoryCache();

        // 清理最新案例缓存
        portalCaseCacheService.delLatestCaseCache();

        logger.info("案例创建缓存清理完成");
    }

    /**
     * 处理案例更新
     */
    private void handleCaseUpdate(CacheUpdateMessage message) {
        logger.info("处理案例更新事件，案例ID: {}", message.getResourceId());

        try {
            Long caseId = Long.parseLong(message.getResourceId());

            // 清理案例详情缓存
            portalCaseCacheService.delCaseDetailCache(caseId);

            // 清理热门案例缓存
            portalCaseCacheService.delHotCaseCache();

            // 清理最新案例缓存
            portalCaseCacheService.delLatestCaseCache();

            logger.info("案例更新缓存清理完成");

        } catch (NumberFormatException e) {
            logger.error("案例ID格式错误: {}", message.getResourceId());
        }
    }

    /**
     * 处理案例删除
     */
    private void handleCaseDelete(CacheUpdateMessage message) {
        logger.info("处理案例删除事件，案例ID: {}", message.getResourceId());

        try {
            Long caseId = Long.parseLong(message.getResourceId());

            // 清理案例详情缓存
            portalCaseCacheService.delCaseDetailCache(caseId);

            // 清理分类列表缓存
            portalCaseCacheService.delCaseCategoryCache();

            // 清理热门案例缓存
            portalCaseCacheService.delHotCaseCache();

            // 清理最新案例缓存
            portalCaseCacheService.delLatestCaseCache();

            logger.info("案例删除缓存清理完成");

        } catch (NumberFormatException e) {
            logger.error("案例ID格式错误: {}", message.getResourceId());
        }
    }

    /**
     * 处理案例批量删除
     */
    private void handleCaseBatchDelete(CacheUpdateMessage message) {
        logger.info("处理案例批量删除事件，案例ID列表: {}", message.getResourceId());

        // 清理所有相关缓存
        portalCaseCacheService.delAllCache();

        logger.info("案例批量删除缓存清理完成");
    }

    /**
     * 处理案例状态更新
     */
    private void handleCaseStatusUpdate(CacheUpdateMessage message) {
        logger.info("处理案例状态更新事件，案例ID: {}", message.getResourceId());

        try {
            Long caseId = Long.parseLong(message.getResourceId());

            // 清理案例详情缓存
            portalCaseCacheService.delCaseDetailCache(caseId);

            // 清理热门案例缓存
            portalCaseCacheService.delHotCaseCache();

            // 清理最新案例缓存
            portalCaseCacheService.delLatestCaseCache();

            logger.info("案例状态更新缓存清理完成");

        } catch (NumberFormatException e) {
            logger.error("案例ID格式错误: {}", message.getResourceId());
        }
    }
}