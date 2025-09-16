package com.macro.mall.portal.service;

import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListResult;

import java.util.List;

/**
 * 前台案例缓存管理Service
 */
public interface PortalCaseCacheService {
    
    /**
     * 获取缓存的案例分类列表
     */
    List<CaseCategory> getCaseCategoryList();

    /**
     * 设置案例分类列表缓存
     */
    void setCaseCategoryList(List<CaseCategory> caseCategoryList);

    /**
     * 获取缓存的案例详情
     */
    CaseDetailResult getCaseDetail(Long caseId);

    /**
     * 设置案例详情缓存
     */
    void setCaseDetail(CaseDetailResult caseDetail);

    /**
     * 获取缓存的热门案例列表
     */
    List<CaseListResult> getHotCaseList(Integer size);

    /**
     * 设置热门案例列表缓存
     */
    void setHotCaseList(List<CaseListResult> hotCaseList, Integer size);

    /**
     * 获取缓存的最新案例列表
     */
    List<CaseListResult> getLatestCaseList(Integer size);

    /**
     * 设置最新案例列表缓存
     */
    void setLatestCaseList(List<CaseListResult> latestCaseList, Integer size);

    /**
     * 删除所有缓存
     */
    void delAllCache();
}