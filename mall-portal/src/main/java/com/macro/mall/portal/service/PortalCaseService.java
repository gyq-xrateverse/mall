package com.macro.mall.portal.service;

import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;

import java.util.List;

/**
 * 前台案例管理Service
 */
public interface PortalCaseService {
    
    /**
     * 获取案例分类列表
     */
    List<CaseCategory> getCategoryList();

    /**
     * 分页获取案例列表
     */
    List<CaseListResult> getCaseList(CaseListQueryParam queryParam);

    /**
     * 获取案例详情
     */
    CaseDetailResult getCaseDetail(Long id);

    /**
     * 获取热门案例列表
     */
    List<CaseListResult> getHotCaseList(Integer size);

    /**
     * 获取最新案例列表
     */
    List<CaseListResult> getLatestCaseList(Integer size);

    /**
     * 案例点赞
     */
    int likeCase(Long id);

    /**
     * 增加案例浏览量
     */
    int viewCase(Long id);

    /**
     * 懒加载获取案例列表
     */
    List<CaseListResult> getLazyCaseList(Long lastId, Integer pageSize, Long categoryId, String queryType);

    /**
     * 无限滚动获取更多案例
     */
    List<CaseListResult> getMoreCases(Long lastId, Integer pageSize, Long categoryId);
}