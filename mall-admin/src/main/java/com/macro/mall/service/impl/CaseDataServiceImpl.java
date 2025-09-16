package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.mapper.CaseDataMapper;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;
import com.macro.mall.model.CaseDataExample;
import com.macro.mall.service.CaseDataService;
import com.macro.mall.common.service.FileStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 案例数据管理Service实现类
 */
@Service
public class CaseDataServiceImpl implements CaseDataService {

    @Autowired
    private CaseDataMapper caseDataMapper;

    @Autowired
    private CaseCategoryMapper caseCategoryMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Override
    public int create(CaseDataParam caseDataParam) {
        CaseData caseData = new CaseData();
        BeanUtils.copyProperties(caseDataParam, caseData);

        // 处理标签转换：从List<String>转为逗号分隔的字符串
        if (caseDataParam.getTagList() != null && !caseDataParam.getTagList().isEmpty()) {
            String tagsString = String.join(",", caseDataParam.getTagList());
            caseData.setTags(tagsString);
        } else {
            caseData.setTags("");
        }

        caseData.setCreateTime(new Date());
        caseData.setUpdateTime(new Date());
        if (caseData.getStatus() == null) {
            caseData.setStatus(1);
        }
        if (caseData.getShowStatus() == null) {
            caseData.setShowStatus(1);
        }
        if (caseData.getViewCount() == null) {
            caseData.setViewCount(0L);
        }
        if (caseData.getLikeCount() == null) {
            caseData.setLikeCount(0L);
        }
        if (caseData.getHotScore() == null) {
            caseData.setHotScore(new BigDecimal("0"));
        }

        return caseDataMapper.insertSelective(caseData);
    }

    @Override
    public int update(Long id, CaseDataParam caseDataParam) {
        CaseData caseData = new CaseData();
        BeanUtils.copyProperties(caseDataParam, caseData);

        // 处理标签转换：从List<String>转为逗号分隔的字符串
        if (caseDataParam.getTagList() != null && !caseDataParam.getTagList().isEmpty()) {
            String tagsString = String.join(",", caseDataParam.getTagList());
            caseData.setTags(tagsString);
        } else {
            caseData.setTags("");
        }

        caseData.setId(id);
        caseData.setUpdateTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }

    @Override
    public List<CaseDataResult> list(CaseDataQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPageNum(), queryParam.getPageSize());
        CaseDataExample example = new CaseDataExample();
        CaseDataExample.Criteria criteria = example.createCriteria();

        if (queryParam.getCategoryId() != null) {
            criteria.andCategoryIdEqualTo(queryParam.getCategoryId());
        }
        if (StringUtils.hasText(queryParam.getTitle())) {
            criteria.andTitleLike("%" + queryParam.getTitle() + "%");
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus());
        }
        if (queryParam.getShowStatus() != null) {
            criteria.andShowStatusEqualTo(queryParam.getShowStatus());
        }

        if (StringUtils.hasText(queryParam.getSortField()) && StringUtils.hasText(queryParam.getSortOrder())) {
            String orderBy = convertSortField(queryParam.getSortField()) + " " + queryParam.getSortOrder();
            example.setOrderByClause(orderBy);
        } else {
            example.setOrderByClause("create_time DESC");
        }

        List<CaseData> caseDataList = caseDataMapper.selectByExample(example);
        return convertToResult(caseDataList);
    }

    @Override
    public int delete(Long id) {
        return caseDataMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBatch(List<Long> ids) {
        CaseDataExample example = new CaseDataExample();
        example.createCriteria().andIdIn(ids);
        return caseDataMapper.deleteByExample(example);
    }

    @Override
    public CaseDataResult getItem(Long id) {
        CaseData caseData = caseDataMapper.selectByPrimaryKey(id);
        if (caseData == null) {
            return null;
        }
        return convertToResult(caseData);
    }

    @Override
    public int updateStatus(List<Long> ids, Integer status) {
        CaseData record = new CaseData();
        record.setStatus(status);
        record.setUpdateTime(new Date());
        CaseDataExample example = new CaseDataExample();
        example.createCriteria().andIdIn(ids);
        return caseDataMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        CaseData record = new CaseData();
        record.setShowStatus(showStatus);
        record.setUpdateTime(new Date());
        CaseDataExample example = new CaseDataExample();
        example.createCriteria().andIdIn(ids);
        return caseDataMapper.updateByExampleSelective(record, example);
    }

    @Override
    public int approve(Long id, Integer status) {
        CaseData record = new CaseData();
        record.setId(id);
        record.setStatus(status);
        record.setUpdateTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(record);
    }

    private List<CaseDataResult> convertToResult(List<CaseData> caseDataList) {
        List<CaseDataResult> resultList = new ArrayList<>();
        for (CaseData caseData : caseDataList) {
            resultList.add(convertToResult(caseData));
        }
        return resultList;
    }

    private CaseDataResult convertToResult(CaseData caseData) {
        CaseDataResult result = new CaseDataResult();
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

        // 构建图片和视频的完整URL
        if (StringUtils.hasText(caseData.getImage())) {
            result.setImage(fileStorageService.buildUrl(caseData.getImage()));
        }

        // 构建视频URL
        if (StringUtils.hasText(caseData.getVideo())) {
            result.setVideo(fileStorageService.buildUrl(caseData.getVideo()));
        }

        return result;
    }

    private String convertSortField(String sortField) {
        switch (sortField) {
            case "create_time":
                return "create_time";
            case "hot_score":
                return "hot_score";
            case "view_count":
                return "view_count";
            case "like_count":
                return "like_count";
            default:
                return "create_time";
        }
    }
}
