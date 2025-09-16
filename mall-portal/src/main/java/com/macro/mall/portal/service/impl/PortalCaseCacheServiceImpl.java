package com.macro.mall.portal.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.service.PortalCaseCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台案例缓存管理Service实现类
 */
@Service
public class PortalCaseCacheServiceImpl implements PortalCaseCacheService {
    
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
        String categoryKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        String hotKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";
        String latestKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";
        String detailKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail*";
        
        redisService.del(categoryKey);
    }
}