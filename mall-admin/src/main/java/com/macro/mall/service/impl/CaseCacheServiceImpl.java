package com.macro.mall.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.constant.CacheKeyConstants;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.component.CacheSecurityGuard;
import com.macro.mall.dto.CacheUpdateMessage;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;
import com.macro.mall.service.CaseCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 案例缓存管理Service实现类
 */
@Service
public class CaseCacheServiceImpl implements CaseCacheService {

    private static final Logger logger = LoggerFactory.getLogger(CaseCacheServiceImpl.class);

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CacheSecurityGuard cacheSecurityGuard;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;

    @Value("${redis.expire.case}")
    private Long REDIS_EXPIRE_CASE;

    @Value("${redis.key.case}")
    private String REDIS_KEY_CASE;

    @Override
    public void delCaseCategory(Long categoryId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:" + categoryId;
        redisService.del(key);
    }

    @Override
    public void delCaseCategoryList() {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:list";
        redisService.del(key);
    }

    @Override
    public CaseCategory getCaseCategory(Long categoryId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:" + categoryId;
        return (CaseCategory) redisService.get(key);
    }

    @Override
    public void setCaseCategory(CaseCategory caseCategory) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:" + caseCategory.getId();
        redisService.set(key, caseCategory, REDIS_EXPIRE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CaseCategory> getCaseCategoryList() {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:list";
        return (List<CaseCategory>) redisService.get(key);
    }

    @Override
    public void setCaseCategoryList(List<CaseCategory> caseCategoryList) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:list";
        redisService.set(key, caseCategoryList, REDIS_EXPIRE);
    }

    @Override
    public void delCaseData(Long caseId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:" + caseId;
        redisService.del(key);
    }

    @Override
    public void delCaseDataList() {
        String listKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:list*";
        String hotKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:hot";
        String latestKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:latest";
        redisService.del(hotKey);
        redisService.del(latestKey);
    }

    @Override
    public CaseData getCaseData(Long caseId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:" + caseId;
        return (CaseData) redisService.get(key);
    }

    @Override
    public void setCaseData(CaseData caseData) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:" + caseData.getId();
        redisService.set(key, caseData, REDIS_EXPIRE_CASE);
    }

    @Override
    public void delHotCaseList() {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:hot";
        redisService.del(key);
    }

    @Override
    public void delLatestCaseList() {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:latest";
        redisService.del(key);
    }

    @Override
    public void batchClearCaseCache(Long caseId, String operator) {
        try {
            logger.info("开始批量清理案例缓存，案例ID: {}, 操作人: {}", caseId, operator);

            delCaseData(caseId);

            logger.info("案例缓存清理完成，案例ID: {}", caseId);
        } catch (Exception e) {
            logger.error("批量清理案例缓存失败，案例ID: {}, 错误: {}", caseId, e.getMessage(), e);
        }
    }

    @Override
    public void batchClearCaseListCache(String operator) {
        try {
            logger.info("开始批量清理案例列表缓存，操作人: {}", operator);

            delCaseCategoryList();
            delCaseDataList();
            delHotCaseList();
            delLatestCaseList();

            logger.info("案例列表缓存清理完成");
        } catch (Exception e) {
            logger.error("批量清理案例列表缓存失败，错误: {}", e.getMessage(), e);
        }
    }

    @Override
    public void publishCacheUpdateMessage(CacheUpdateMessage message) {
        try {
            // 验证消息操作人员权限
            if (!cacheSecurityGuard.checkPermission(message.getOperator(), "MESSAGE_PUBLISH")) {
                cacheSecurityGuard.logSecurityEvent(message.getOperator(), "MESSAGE_PUBLISH", "PERMISSION_DENIED",
                    "用户尝试发布缓存更新消息但权限不足");
                return;
            }

            // 检查发布频率
            if (!cacheSecurityGuard.checkRateLimit(message.getOperator(), "MESSAGE_PUBLISH")) {
                cacheSecurityGuard.logSecurityEvent(message.getOperator(), "MESSAGE_PUBLISH", "RATE_LIMIT_EXCEEDED",
                    "消息发布频率超限");
                return;
            }

            String messageJson = objectMapper.writeValueAsString(message);

            // 验证消息内容
            if (!cacheSecurityGuard.validateMessageSource(messageJson)) {
                cacheSecurityGuard.logSecurityEvent(message.getOperator(), "MESSAGE_PUBLISH", "INVALID_MESSAGE",
                    "消息格式验证失败");
                return;
            }

            redisTemplate.convertAndSend(CacheKeyConstants.CACHE_UPDATE_CHANNEL, messageJson);

            logger.info("发布缓存更新消息成功: {}", message);
        } catch (Exception e) {
            logger.error("发布缓存更新消息失败: {}, 错误: {}", message, e.getMessage(), e);
            if (message != null && message.getOperator() != null) {
                cacheSecurityGuard.logSecurityEvent(message.getOperator(), "MESSAGE_PUBLISH", "OPERATION_FAILED",
                    "消息发布失败: " + e.getMessage());
            }
        }
    }

    @Override
    public void clearCacheForCaseCreate(Long caseId, String operator) {
        try {
            logger.info("开始清理案例创建相关缓存，案例ID: {}, 操作人: {}", caseId, operator);

            // 清理管理端缓存
            delCaseCategoryList();
            delLatestCaseList();

            // 直接清理用户端缓存
            clearPortalCache(caseId);

            logger.info("案例创建缓存清理完成，案例ID: {}, 操作人: {}", caseId, operator);
        } catch (Exception e) {
            logger.error("案例创建缓存清理失败，案例ID: {}, 操作人: {}, 错误: {}", caseId, operator, e.getMessage(), e);
        }
    }

    @Override
    public void clearCacheForCaseUpdate(Long caseId, String operator) {
        try {
            logger.info("开始清理案例更新相关缓存，案例ID: {}, 操作人: {}", caseId, operator);

            // 清理管理端缓存
            delCaseData(caseId);
            delCaseDataList();
            delHotCaseList();
            delLatestCaseList();

            // 直接清理用户端缓存 - 最简洁的解决方案
            clearPortalCache(caseId);

            logger.info("案例更新缓存清理完成，案例ID: {}, 操作人: {}", caseId, operator);
        } catch (Exception e) {
            logger.error("案例更新缓存清理失败，案例ID: {}, 操作人: {}, 错误: {}", caseId, operator, e.getMessage(), e);
        }
    }

    @Override
    public void clearCacheForCaseDelete(Long caseId, String operator) {
        try {
            logger.info("开始清理案例删除相关缓存，案例ID: {}, 操作人: {}", caseId, operator);

            // 清理管理端缓存
            delCaseData(caseId);
            delCaseCategoryList();
            delCaseDataList();
            delHotCaseList();
            delLatestCaseList();

            // 直接清理用户端缓存
            clearPortalCache(caseId);

            logger.info("案例删除缓存清理完成，案例ID: {}, 操作人: {}", caseId, operator);
        } catch (Exception e) {
            logger.error("案例删除缓存清理失败，案例ID: {}, 操作人: {}, 错误: {}", caseId, operator, e.getMessage(), e);
        }
    }

    @Override
    public void clearCacheForCaseBatchDelete(List<Long> caseIds, String operator) {
        try {
            logger.info("开始清理案例批量删除相关缓存，案例ID列表: {}, 操作人: {}", caseIds, operator);

            // 清理管理端缓存
            for (Long caseId : caseIds) {
                delCaseData(caseId);
                // 每个案例也清理用户端缓存
                clearPortalCache(caseId);
            }
            delCaseCategoryList();
            delCaseDataList();
            delHotCaseList();
            delLatestCaseList();

            logger.info("案例批量删除缓存清理完成，案例ID列表: {}, 操作人: {}", caseIds, operator);
        } catch (Exception e) {
            logger.error("案例批量删除缓存清理失败，案例ID列表: {}, 操作人: {}, 错误: {}", caseIds, operator, e.getMessage(), e);
        }
    }

    @Override
    public void clearCacheForCaseStatusUpdate(Long caseId, String operator) {
        try {
            logger.info("开始清理案例状态更新相关缓存，案例ID: {}, 操作人: {}", caseId, operator);

            // 清理管理端缓存
            delCaseData(caseId);
            delCaseDataList();
            delHotCaseList();
            delLatestCaseList();

            // 直接清理用户端缓存
            clearPortalCache(caseId);

            logger.info("案例状态更新缓存清理完成，案例ID: {}, 操作人: {}", caseId, operator);
        } catch (Exception e) {
            logger.error("案例状态更新缓存清理失败，案例ID: {}, 操作人: {}, 错误: {}", caseId, operator, e.getMessage(), e);
        }
    }

    /**
     * 直接清理用户端缓存 - 最简洁的解决方案
     */
    private void clearPortalCache(Long caseId) {
        try {
            logger.info("开始直接清理用户端缓存，案例ID: {}", caseId);

            // 清理用户端案例详情缓存
            if (caseId != null) {
                String detailKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
                redisService.del(detailKey);
                logger.debug("清理用户端案例详情缓存: {}", detailKey);
            }

            // 清理用户端分类列表缓存
            String categoryKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
            redisService.del(categoryKey);
            logger.debug("清理用户端分类列表缓存: {}", categoryKey);

            // 清理用户端热门案例缓存（所有size的缓存）
            String hotPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";
            redisService.delByPattern(hotPattern);
            logger.debug("清理用户端热门案例缓存: {}", hotPattern);

            // 清理用户端最新案例缓存（所有size的缓存）
            String latestPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";
            redisService.delByPattern(latestPattern);
            logger.debug("清理用户端最新案例缓存: {}", latestPattern);

            logger.info("用户端缓存直接清理完成，案例ID: {}", caseId);
        } catch (Exception e) {
            logger.error("直接清理用户端缓存失败，案例ID: {}, 错误: {}", caseId, e.getMessage(), e);
        }
    }
}