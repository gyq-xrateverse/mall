package com.macro.mall.service;

import com.macro.mall.dto.CaseCategoryParam;
import com.macro.mall.dto.CaseCategoryQueryParam;
import com.macro.mall.model.CaseCategory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 案例分类管理Service
 */
public interface CaseCategoryService {
    
    /**
     * 创建案例分类
     */
    @Transactional
    int create(CaseCategoryParam caseCategoryParam);

    /**
     * 修改案例分类
     */
    @Transactional
    int update(Long id, CaseCategoryParam caseCategoryParam);

    /**
     * 分页获取案例分类列表
     */
    List<CaseCategory> list(CaseCategoryQueryParam queryParam);

    /**
     * 删除案例分类
     */
    int delete(Long id);

    /**
     * 批量删除案例分类
     */
    int deleteBatch(List<Long> ids);

    /**
     * 根据ID获取案例分类
     */
    CaseCategory getItem(Long id);

    /**
     * 批量修改显示状态
     */
    int updateShowStatus(List<Long> ids, Integer showStatus);

    /**
     * 获取所有启用的案例分类
     */
    List<CaseCategory> listAll();
}