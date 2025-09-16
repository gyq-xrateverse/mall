package com.macro.mall.service;

import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 案例数据管理Service
 */
public interface CaseDataService {
    
    /**
     * 创建案例数据
     */
    @Transactional
    int create(CaseDataParam caseDataParam);

    /**
     * 修改案例数据
     */
    @Transactional
    int update(Long id, CaseDataParam caseDataParam);

    /**
     * 分页获取案例数据列表
     */
    List<CaseDataResult> list(CaseDataQueryParam queryParam);

    /**
     * 删除案例数据
     */
    int delete(Long id);

    /**
     * 批量删除案例数据
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID获取案例数据详情
     */
    CaseDataResult getItem(Long id);

    /**
     * 批量修改状态
     */
    int updateStatus(List<Long> ids, Integer status);

    /**
     * 批量修改显示状态
     */
    int updateShowStatus(List<Long> ids, Integer showStatus);

    /**
     * 审核案例
     */
    int approve(Long id, Integer status);
}