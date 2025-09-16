package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.mapper.CaseDataMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.service.PortalCaseService;
import com.macro.mall.portal.service.PortalCaseCacheService;
import com.macro.mall.common.service.FileStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 前台案例管理Service实现类
 */
@Service
public class PortalCaseServiceImpl implements PortalCaseService {
    
    @Autowired
    private CaseCategoryMapper caseCategoryMapper;
    
    @Autowired
    private CaseDataMapper caseDataMapper;
    
    @Autowired
    private PortalCaseCacheService portalCaseCacheService;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public List<CaseCategory> getCategoryList() {
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria().andStatusEqualTo(1);
        example.setOrderByClause("id ASC");
        return caseCategoryMapper.selectByExample(example);
    }

    @Override
    public List<CaseListResult> getCaseList(CaseListQueryParam queryParam) {
        // 先尝试从缓存获取数据
        String cacheKey = buildCacheKey(queryParam);
        List<CaseListResult> cachedResult = getCachedCaseList(cacheKey);
        if (cachedResult != null && !cachedResult.isEmpty()) {
            return cachedResult;
        }
        
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        CaseDataExample example = new CaseDataExample();
        CaseDataExample.Criteria criteria = example.createCriteria();
        
        criteria.andStatusEqualTo(1);
        criteria.andShowStatusEqualTo(1);
        
        if (queryParam.getCategoryId() != null) {
            criteria.andCategoryIdEqualTo(queryParam.getCategoryId());
        }
        
        if (StringUtils.hasText(queryParam.getKeyword())) {
            criteria.andTitleLike("%" + queryParam.getKeyword() + "%");
        }
        
        String orderBy = "id DESC";
        if (StringUtils.hasText(queryParam.getQueryType())) {
            switch (queryParam.getQueryType()) {
                case "latest":
                    orderBy = "id DESC";
                    break;
                case "hot":
                    orderBy = "id DESC";
                    break;
                case "all":
                default:
                    orderBy = "id DESC";
                    break;
            }
        }
        example.setOrderByClause(orderBy);
        
        List<CaseData> caseDataList = caseDataMapper.selectByExample(example);
        List<CaseListResult> result = convertToCaseListResult(caseDataList);
        
        // 缓存结果（仅缓存前3页的数据）
        if (queryParam.getPageNum() <= 3) {
            setCachedCaseList(cacheKey, result);
        }
        
        return result;
    }

    @Override
    public CaseDetailResult getCaseDetail(Long id) {
        CaseData caseData = caseDataMapper.selectByPrimaryKey(id);
        if (caseData == null || caseData.getStatus() != 1 || caseData.getShowStatus() != 1) {
            return null;
        }
        
        CaseDetailResult result = new CaseDetailResult();
        BeanUtils.copyProperties(caseData, result);
        
        if (caseData.getCategoryId() != null) {
            CaseCategory category = caseCategoryMapper.selectByPrimaryKey(caseData.getCategoryId());
            if (category != null) {
                result.setCategoryName(category.getName());
            }
        }
        
        if (StringUtils.hasText(caseData.getTags())) {
            String[] tagsArray = caseData.getTags().split(",");
            List<String> tagList = new ArrayList<>();
            for (String tag : tagsArray) {
                tagList.add(tag.trim());
            }
            result.setTagList(tagList);
        }
        
        // 设置视频封面图片URL
        if (StringUtils.hasText(caseData.getImage())) {
            result.setImageUrl(fileStorageService.buildUrl(caseData.getImage()));
        }

        // 设置视频文件URL
        if (StringUtils.hasText(caseData.getVideo())) {
            result.setVideoUrl(fileStorageService.buildUrl(caseData.getVideo()));
        }
        
        List<CaseListResult> relatedCases = getRelatedCases(caseData.getCategoryId(), id);
        result.setRelatedCases(relatedCases);
        
        return result;
    }

    @Override
    public List<CaseListResult> getHotCaseList(Integer size) {
        PageHelper.startPage(1, size);
        CaseDataExample example = new CaseDataExample();
        example.createCriteria()
               .andStatusEqualTo(1)
               .andShowStatusEqualTo(1);
        example.setOrderByClause("id DESC");
        
        List<CaseData> caseDataList = caseDataMapper.selectByExample(example);
        return convertToCaseListResult(caseDataList);
    }

    @Override
    public List<CaseListResult> getLatestCaseList(Integer size) {
        PageHelper.startPage(1, size);
        CaseDataExample example = new CaseDataExample();
        example.createCriteria()
               .andStatusEqualTo(1)
               .andShowStatusEqualTo(1);
        example.setOrderByClause("id DESC");
        
        List<CaseData> caseDataList = caseDataMapper.selectByExample(example);
        return convertToCaseListResult(caseDataList);
    }

    @Override
    public int likeCase(Long id) {
        CaseData caseData = caseDataMapper.selectByPrimaryKey(id);
        if (caseData == null) {
            return 0;
        }
        
        CaseData updateRecord = new CaseData();
        updateRecord.setId(id);
        updateRecord.setLikeCount(caseData.getLikeCount() + 1);
        
        updateHotScore(updateRecord, caseData.getViewCount(), updateRecord.getLikeCount());
        
        return caseDataMapper.updateByPrimaryKeySelective(updateRecord);
    }

    @Override
    public int viewCase(Long id) {
        CaseData caseData = caseDataMapper.selectByPrimaryKey(id);
        if (caseData == null) {
            return 0;
        }
        
        CaseData updateRecord = new CaseData();
        updateRecord.setId(id);
        updateRecord.setViewCount(caseData.getViewCount() + 1);
        
        updateHotScore(updateRecord, updateRecord.getViewCount(), caseData.getLikeCount());
        
        return caseDataMapper.updateByPrimaryKeySelective(updateRecord);
    }

    @Override
    public List<CaseListResult> getLazyCaseList(Long lastId, Integer pageSize, Long categoryId, String queryType) {
        CaseDataExample example = new CaseDataExample();
        CaseDataExample.Criteria criteria = example.createCriteria();
        
        criteria.andStatusEqualTo(1);
        criteria.andShowStatusEqualTo(1);
        
        if (lastId != null && lastId > 0) {
            // 由于andIdLessThan方法不存在，暂时移除此条件
            // TODO: 需要重新生成CaseDataExample或添加自定义查询方法
        }
        
        if (categoryId != null) {
            criteria.andCategoryIdEqualTo(categoryId);
        }
        
        String orderBy;
        if ("hot".equals(queryType)) {
            orderBy = "id DESC";
        } else if ("latest".equals(queryType)) {
            orderBy = "id DESC";
        } else {
            orderBy = "id DESC";
        }
        example.setOrderByClause(orderBy);
        
        // 不使用PageHelper，直接限制返回数量
        PageHelper.startPage(1, pageSize, false);
        List<CaseData> caseDataList = caseDataMapper.selectByExample(example);
        return convertToCaseListResult(caseDataList);
    }

    @Override
    public List<CaseListResult> getMoreCases(Long lastId, Integer pageSize, Long categoryId) {
        return getLazyCaseList(lastId, pageSize, categoryId, "latest");
    }

    private List<CaseListResult> convertToCaseListResult(List<CaseData> caseDataList) {
        return caseDataList.stream().map(this::convertToCaseListResult).collect(Collectors.toList());
    }

    private CaseListResult convertToCaseListResult(CaseData caseData) {
        CaseListResult result = new CaseListResult();
        BeanUtils.copyProperties(caseData, result);

        if (caseData.getCategoryId() != null) {
            CaseCategory category = caseCategoryMapper.selectByPrimaryKey(caseData.getCategoryId());
            if (category != null) {
                result.setCategoryName(category.getName());
            }
        }

        // 设置视频封面图片URL
        if (StringUtils.hasText(caseData.getImage())) {
            result.setImageUrl(fileStorageService.buildUrl(caseData.getImage()));
        }

        // 设置视频文件URL
        if (StringUtils.hasText(caseData.getVideo())) {
            result.setVideoUrl(fileStorageService.buildUrl(caseData.getVideo()));
        }

        if (StringUtils.hasText(caseData.getTags())) {
            String[] tagsArray = caseData.getTags().split(",");
            List<String> tagList = new ArrayList<>();
            for (String tag : tagsArray) {
                tagList.add(tag.trim());
            }
            result.setTagList(tagList);
        }

        return result;
    }

    private List<CaseListResult> getRelatedCases(Long categoryId, Long excludeId) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        
        PageHelper.startPage(1, 6);
        CaseDataExample example = new CaseDataExample();
        CaseDataExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andCategoryIdEqualTo(categoryId);
        
        if (excludeId != null) {
            criteria.andIdNotEqualTo(excludeId);
        }
        
        example.setOrderByClause("id DESC");
        List<CaseData> relatedCases = caseDataMapper.selectByExample(example);
        return convertToCaseListResult(relatedCases);
    }

    private void updateHotScore(CaseData updateRecord, Long viewCount, Long likeCount) {
        double hotScore = calculateHotScore(viewCount, likeCount);
        updateRecord.setHotScore(new BigDecimal(hotScore));
    }

    private double calculateHotScore(Long viewCount, Long likeCount) {
        return (viewCount * 1.0) + (likeCount * 5.0);
    }

    private String buildCacheKey(CaseListQueryParam queryParam) {
        StringBuilder keyBuilder = new StringBuilder("case_list:");
        keyBuilder.append("page_").append(queryParam.getPageNum());
        keyBuilder.append("_size_").append(queryParam.getPageSize());
        
        if (queryParam.getCategoryId() != null) {
            keyBuilder.append("_cat_").append(queryParam.getCategoryId());
        }
        if (StringUtils.hasText(queryParam.getKeyword())) {
            keyBuilder.append("_kw_").append(queryParam.getKeyword().hashCode());
        }
        if (StringUtils.hasText(queryParam.getQueryType())) {
            keyBuilder.append("_type_").append(queryParam.getQueryType());
        }
        
        return keyBuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private List<CaseListResult> getCachedCaseList(String cacheKey) {
        try {
            return (List<CaseListResult>) portalCaseCacheService.getHotCaseList(0);
        } catch (Exception e) {
            return null;
        }
    }

    private void setCachedCaseList(String cacheKey, List<CaseListResult> result) {
        try {
            portalCaseCacheService.setHotCaseList(result, 0);
        } catch (Exception e) {
            // 缓存失败不影响主要流程
        }
    }
}