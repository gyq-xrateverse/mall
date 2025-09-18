package com.macro.mall.portal.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.constant.CacheKeyConstants;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.service.PortalCaseCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台案例缓存管理Service实现类
 */
@Service
public class PortalCaseCacheServiceImpl implements PortalCaseCacheService {

    private static final Logger logger = LoggerFactory.getLogger(PortalCaseCacheServiceImpl.class);

    @Autowired
    private RedisService redisService;

    @Value("${redis.database}")
    private String REDIS_DATABASE;

    @Value("${redis.expire.common}")
    private Long REDIS_EXPIRE;

    @Value("${redis.expire.case}")
    private Long REDIS_EXPIRE_CASE;

    @Value("${redis.key.case}")
    private String REDIS_KEY_CASE;

    @Override
    @SuppressWarnings("unchecked")
    public List<CaseCategory> getCaseCategoryList() {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        return (List<CaseCategory>) redisService.get(key);
    }

    @Override
    public void setCaseCategoryList(List<CaseCategory> caseCategoryList) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        redisService.set(key, caseCategoryList, REDIS_EXPIRE);
    }

    @Override
    public CaseDetailResult getCaseDetail(Long caseId) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
        return (CaseDetailResult) redisService.get(key);
    }

    @Override
    public void setCaseDetail(CaseDetailResult caseDetail) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseDetail.getId();
        redisService.set(key, caseDetail, REDIS_EXPIRE_CASE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CaseListResult> getHotCaseList(Integer size) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot:" + size;
        return (List<CaseListResult>) redisService.get(key);
    }

    @Override
    public void setHotCaseList(List<CaseListResult> hotCaseList, Integer size) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot:" + size;
        redisService.set(key, hotCaseList, REDIS_EXPIRE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CaseListResult> getLatestCaseList(Integer size) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest:" + size;
        return (List<CaseListResult>) redisService.get(key);
    }

    @Override
    public void setLatestCaseList(List<CaseListResult> latestCaseList, Integer size) {
        String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest:" + size;
        redisService.set(key, latestCaseList, REDIS_EXPIRE);
    }

    @Override
    public void delAllCache() {
        try {
            logger.info("开始清理门户端所有案例缓存");

            String categoryKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
            String hotPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";
            String latestPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";
            String detailPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail*";

            redisService.del(categoryKey);
            redisService.delByPattern(hotPattern);
            redisService.delByPattern(latestPattern);
            redisService.delByPattern(detailPattern);

            logger.info("门户端所有案例缓存清理完成");
        } catch (Exception e) {
            logger.error("清理门户端所有案例缓存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void delCaseCategoryCache() {
        try {
            String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
            redisService.del(key);
            logger.debug("清理门户端案例分类缓存: {}", key);
        } catch (Exception e) {
            logger.error("清理门户端案例分类缓存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void delCaseDetailCache(Long caseId) {
        try {
            String key = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
            redisService.del(key);
            logger.debug("清理门户端案例详情缓存: {}, 案例ID: {}", key, caseId);
        } catch (Exception e) {
            logger.error("清理门户端案例详情缓存失败，案例ID: {}, 错误: {}", caseId, e.getMessage(), e);
        }
    }

    @Override
    public void delHotCaseCache() {
        try {
            String pattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";
            redisService.delByPattern(pattern);
            logger.debug("清理门户端热门案例缓存: {}", pattern);
        } catch (Exception e) {
            logger.error("清理门户端热门案例缓存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public void delLatestCaseCache() {
        try {
            String pattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";
            redisService.delByPattern(pattern);
            logger.debug("清理门户端最新案例缓存: {}", pattern);
        } catch (Exception e) {
            logger.error("清理门户端最新案例缓存失败: {}", e.getMessage(), e);
        }
    }
}