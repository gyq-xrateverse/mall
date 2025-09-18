package com.macro.mall.service;

import com.macro.mall.dto.CacheUpdateMessage;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;

import java.util.List;

/**
 * 案例缓存管理Service
 */
public interface CaseCacheService {
    
    /**
     * 删除案例分类缓存
     */
    void delCaseCategory(Long categoryId);

    /**
     * 删除所有案例分类缓存
     */
    void delCaseCategoryList();

    /**
     * 获取缓存的案例分类
     */
    CaseCategory getCaseCategory(Long categoryId);

    /**
     * 设置案例分类缓存
     */
    void setCaseCategory(CaseCategory caseCategory);

    /**
     * 获取缓存的案例分类列表
     */
    List<CaseCategory> getCaseCategoryList();

    /**
     * 设置案例分类列表缓存
     */
    void setCaseCategoryList(List<CaseCategory> caseCategoryList);

    /**
     * 删除案例数据缓存
     */
    void delCaseData(Long caseId);

    /**
     * 删除案例列表缓存
     */
    void delCaseDataList();

    /**
     * 获取缓存的案例数据
     */
    CaseData getCaseData(Long caseId);

    /**
     * 设置案例数据缓存
     */
    void setCaseData(CaseData caseData);

    /**
     * 删除热门案例缓存
     */
    void delHotCaseList();

    /**
     * 删除最新案例缓存
     */
    void delLatestCaseList();

    /**
     * 批量清理案例相关缓存
     * @param caseId 案例ID
     * @param operator 操作人员
     */
    void batchClearCaseCache(Long caseId, String operator);

    /**
     * 批量清理案例列表缓存
     * @param operator 操作人员
     */
    void batchClearCaseListCache(String operator);

    /**
     * 发布缓存更新消息
     * @param message 缓存更新消息
     */
    void publishCacheUpdateMessage(CacheUpdateMessage message);

    /**
     * 清理案例创建相关缓存并发布消息
     * @param caseId 案例ID
     * @param operator 操作人员
     */
    void clearCacheForCaseCreate(Long caseId, String operator);

    /**
     * 清理案例更新相关缓存并发布消息
     * @param caseId 案例ID
     * @param operator 操作人员
     */
    void clearCacheForCaseUpdate(Long caseId, String operator);

    /**
     * 清理案例删除相关缓存并发布消息
     * @param caseId 案例ID
     * @param operator 操作人员
     */
    void clearCacheForCaseDelete(Long caseId, String operator);

    /**
     * 清理案例批量删除相关缓存并发布消息
     * @param caseIds 案例ID列表
     * @param operator 操作人员
     */
    void clearCacheForCaseBatchDelete(List<Long> caseIds, String operator);

    /**
     * 清理案例状态更新相关缓存并发布消息
     * @param caseId 案例ID
     * @param operator 操作人员
     */
    void clearCacheForCaseStatusUpdate(Long caseId, String operator);
}