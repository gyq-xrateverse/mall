package com.macro.mall.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;
import com.macro.mall.service.CaseCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 案例缓存管理Service实现类
 */
@Service
public class CaseCacheServiceImpl implements CaseCacheService {
    
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
}