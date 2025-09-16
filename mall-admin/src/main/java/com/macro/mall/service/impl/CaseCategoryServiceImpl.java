package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.service.RedisLockService;
import com.macro.mall.dto.CaseCategoryParam;
import com.macro.mall.dto.CaseCategoryQueryParam;
import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseCategoryExample;
import com.macro.mall.service.CaseCacheService;
import com.macro.mall.service.CaseCategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 案例分类管理Service实现类
 */
@Service
public class CaseCategoryServiceImpl implements CaseCategoryService {
    
    @Autowired
    private CaseCategoryMapper caseCategoryMapper;
    
    @Autowired
    private CaseCacheService caseCacheService;
    
    @Autowired
    private RedisLockService redisLockService;

    @Override
    public int create(CaseCategoryParam caseCategoryParam) {
        CaseCategory caseCategory = new CaseCategory();
        BeanUtils.copyProperties(caseCategoryParam, caseCategory);
        caseCategory.setCreateTime(new Date());
        caseCategory.setUpdateTime(new Date());
        if (caseCategory.getShowStatus() == null) {
            caseCategory.setShowStatus(1);
        }
        int count = caseCategoryMapper.insertSelective(caseCategory);
        if (count > 0) {
            caseCacheService.delCaseCategoryList();
        }
        return count;
    }

    @Override
    public int update(Long id, CaseCategoryParam caseCategoryParam) {
        CaseCategory caseCategory = new CaseCategory();
        BeanUtils.copyProperties(caseCategoryParam, caseCategory);
        caseCategory.setId(id);
        caseCategory.setUpdateTime(new Date());
        int count = caseCategoryMapper.updateByPrimaryKeySelective(caseCategory);
        if (count > 0) {
            caseCacheService.delCaseCategory(id);
            caseCacheService.delCaseCategoryList();
        }
        return count;
    }

    @Override
    public List<CaseCategory> list(CaseCategoryQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        CaseCategoryExample example = new CaseCategoryExample();
        CaseCategoryExample.Criteria criteria = example.createCriteria();
        
        if (StringUtils.hasText(queryParam.getName())) {
            criteria.andNameLike("%" + queryParam.getName() + "%");
        }
        if (queryParam.getShowStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getShowStatus());
        }
        
        example.setOrderByClause("id ASC");
        
        return caseCategoryMapper.selectByExample(example);
    }

    @Override
    public int delete(Long id) {
        return caseCategoryMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBatch(List<Long> ids) {
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria().andIdIn(ids);
        return caseCategoryMapper.deleteByExample(example);
    }

    @Override
    public CaseCategory getItem(Long id) {
        return caseCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        CaseCategory record = new CaseCategory();
        record.setShowStatus(showStatus);
        record.setUpdateTime(new Date());
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria().andIdIn(ids);
        return caseCategoryMapper.updateByExampleSelective(record, example);
    }

    @Override
    public List<CaseCategory> listAll() {
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria().andStatusEqualTo(1);
        example.setOrderByClause("id ASC");
        return caseCategoryMapper.selectByExample(example);
    }

}