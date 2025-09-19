# 05-案例数据管理API

## 任务概述
在mall-admin模块中实现案例数据的管理接口，包括增删改查、状态管理、批量操作等功能。

## 前置条件
- 案例分类管理API已完成
- 实体类和DTO类已创建
- MinIO文件上传功能可用

## 实施步骤

### 1. 创建Service接口
**文件路径：** `mall-admin/src/main/java/com/macro/mall/service/CaseDataService.java`

```java
package com.macro.mall.service;

import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.github.pagehelper.PageInfo;
import java.math.BigDecimal;
import java.util.List;

/**
 * 案例数据管理Service
 */
public interface CaseDataService {
    
    /**
     * 创建案例
     */
    int create(CaseDataParam param);
    
    /**
     * 修改案例
     */
    int update(Long id, CaseDataParam param);
    
    /**
     * 删除案例
     */
    int delete(Long id);
    
    /**
     * 批量删除案例
     */
    int delete(List<Long> ids);
    
    /**
     * 分页查询案例
     */
    PageInfo<CaseDataResult> list(CaseDataQueryParam param);
    
    /**
     * 获取案例详情
     */
    CaseDataResult getById(Long id);
    
    /**
     * 修改案例状态
     */
    int updateStatus(Long id, Integer status);
    
    /**
     * 修改显示状态
     */
    int updateShowStatus(Long id, Integer showStatus);
    
    /**
     * 更新排序
     */
    int updateSort(Long id, Integer sort);
    
    /**
     * 批量更新热度分数
     */
    int batchUpdateHotScore();
    
    /**
     * 更新指定案例的热度分数
     */
    int updateHotScore(Long id, BigDecimal hotScore);
}
```

### 2. 实现Service类
**文件路径：** `mall-admin/src/main/java/com/macro/mall/service/impl/CaseDataServiceImpl.java`

```java
package com.macro.mall.service.impl;

import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.mapper.CaseDataMapper;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;
import com.macro.mall.model.CaseDataExample;
import com.macro.mall.service.CaseDataService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaseDataServiceImpl implements CaseDataService {
    
    @Autowired
    private CaseDataMapper caseDataMapper;
    
    @Autowired
    private CaseCategoryMapper caseCategoryMapper;
    
    @Override
    public int create(CaseDataParam param) {
        CaseData caseData = new CaseData();
        BeanUtils.copyProperties(param, caseData);
        caseData.setCreateTime(new Date());
        caseData.setDeleteStatus(0);
        if (caseData.getStatus() == null) {
            caseData.setStatus(1);
        }
        if (caseData.getShowStatus() == null) {
            caseData.setShowStatus(1);
        }
        if (caseData.getSort() == null) {
            caseData.setSort(0);
        }
        if (caseData.getLikeCount() == null) {
            caseData.setLikeCount(0);
        }
        if (caseData.getViewCount() == null) {
            caseData.setViewCount(0);
        }
        if (caseData.getHotScore() == null) {
            caseData.setHotScore(BigDecimal.ZERO);
        }
        return caseDataMapper.insert(caseData);
    }
    
    @Override
    public int update(Long id, CaseDataParam param) {
        CaseData caseData = new CaseData();
        BeanUtils.copyProperties(param, caseData);
        caseData.setId(id);
        caseData.setModifyTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }
    
    @Override
    public int delete(Long id) {
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseData.setDeleteStatus(1);
        caseData.setModifyTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }
    
    @Override
    public int delete(List<Long> ids) {
        CaseDataExample example = new CaseDataExample();
        example.createCriteria().andIdIn(ids);
        CaseData caseData = new CaseData();
        caseData.setDeleteStatus(1);
        caseData.setModifyTime(new Date());
        return caseDataMapper.updateByExampleSelective(caseData, example);
    }
    
    @Override
    public PageInfo<CaseDataResult> list(CaseDataQueryParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        CaseDataExample example = new CaseDataExample();
        CaseDataExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        
        if (param.getCategoryId() != null) {
            criteria.andCategoryIdEqualTo(param.getCategoryId());
        }
        if (StringUtils.hasText(param.getTitle())) {
            criteria.andTitleLike("%" + param.getTitle() + "%");
        }
        if (param.getStatus() != null) {
            criteria.andStatusEqualTo(param.getStatus());
        }
        if (param.getShowStatus() != null) {
            criteria.andShowStatusEqualTo(param.getShowStatus());
        }
        
        // 排序
        String orderBy = "sort ASC, create_time DESC";
        if (StringUtils.hasText(param.getSortField())) {
            String sortOrder = StringUtils.hasText(param.getSortOrder()) ? param.getSortOrder() : "desc";
            orderBy = param.getSortField() + " " + sortOrder.toUpperCase() + ", " + orderBy;
        }
        example.setOrderByClause(orderBy);
        
        List<CaseData> list = caseDataMapper.selectByExample(example);
        
        // 转换为Result对象
        List<CaseDataResult> resultList = list.stream().map(this::convertToResult).collect(Collectors.toList());
        
        PageInfo<CaseData> pageInfo = new PageInfo<>(list);
        PageInfo<CaseDataResult> result = new PageInfo<>();
        BeanUtils.copyProperties(pageInfo, result);
        result.setList(resultList);
        
        return result;
    }
    
    @Override
    public CaseDataResult getById(Long id) {
        CaseData caseData = caseDataMapper.selectByPrimaryKey(id);
        return caseData != null ? convertToResult(caseData) : null;
    }
    
    @Override
    public int updateStatus(Long id, Integer status) {
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseData.setStatus(status);
        caseData.setModifyTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }
    
    @Override
    public int updateShowStatus(Long id, Integer showStatus) {
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseData.setShowStatus(showStatus);
        caseData.setModifyTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }
    
    @Override
    public int updateSort(Long id, Integer sort) {
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseData.setSort(sort);
        caseData.setModifyTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }
    
    @Override
    public int batchUpdateHotScore() {
        // 查询所有启用的案例
        CaseDataExample example = new CaseDataExample();
        example.createCriteria()
                .andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andDeleteStatusEqualTo(0);
        
        List<CaseData> caseList = caseDataMapper.selectByExample(example);
        
        int updateCount = 0;
        for (CaseData caseData : caseList) {
            // 计算热度分数
            BigDecimal hotScore = calculateHotScore(caseData);
            if (updateHotScore(caseData.getId(), hotScore) > 0) {
                updateCount++;
            }
        }
        
        return updateCount;
    }
    
    @Override
    public int updateHotScore(Long id, BigDecimal hotScore) {
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseData.setHotScore(hotScore);
        caseData.setHotUpdateTime(new Date());
        return caseDataMapper.updateByPrimaryKeySelective(caseData);
    }
    
    /**
     * 转换为Result对象
     */
    private CaseDataResult convertToResult(CaseData caseData) {
        CaseDataResult result = new CaseDataResult();
        BeanUtils.copyProperties(caseData, result);
        
        // 获取分类名称
        if (caseData.getCategoryId() != null) {
            CaseCategory category = caseCategoryMapper.selectByPrimaryKey(caseData.getCategoryId());
            if (category != null) {
                result.setCategoryName(category.getName());
            }
        }
        
        return result;
    }
    
    /**
     * 计算热度分数
     */
    private BigDecimal calculateHotScore(CaseData caseData) {
        int likeCount = caseData.getLikeCount() != null ? caseData.getLikeCount() : 0;
        int viewCount = caseData.getViewCount() != null ? caseData.getViewCount() : 0;
        Date createTime = caseData.getCreateTime();
        
        // 基础热度 = 点赞数 * 0.7 + 浏览数 * 0.3
        BigDecimal baseScore = BigDecimal.valueOf(likeCount * 0.7 + viewCount * 0.3);
        
        // 时间衰减因子：每7天衰减50%
        if (createTime != null) {
            long daysSinceCreated = (System.currentTimeMillis() - createTime.getTime()) / (24 * 60 * 60 * 1000);
            double timeDecayFactor = Math.pow(0.5, daysSinceCreated / 7.0);
            baseScore = baseScore.multiply(BigDecimal.valueOf(timeDecayFactor));
        }
        
        return baseScore.setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
```

### 3. 创建Controller
**文件路径：** `mall-admin/src/main/java/com/macro/mall/controller/CaseDataController.java`

```java
package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CaseDataParam;
import com.macro.mall.dto.CaseDataQueryParam;
import com.macro.mall.dto.CaseDataResult;
import com.macro.mall.service.CaseDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 案例数据管理Controller
 */
@RestController
@RequestMapping("/case/data")
@Tag(name = "CaseDataController", description = "案例数据管理")
public class CaseDataController {
    
    @Autowired
    private CaseDataService caseDataService;
    
    @Operation(summary = "创建案例")
    @PostMapping("/create")
    public CommonResult create(@Validated @RequestBody CaseDataParam param) {
        int count = caseDataService.create(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "修改案例")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id,
                              @Validated @RequestBody CaseDataParam param) {
        int count = caseDataService.update(id, param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "删除案例")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = caseDataService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "批量删除案例")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = caseDataService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "分页查询案例")
    @GetMapping("/list")
    public CommonResult<CommonPage<CaseDataResult>> list(CaseDataQueryParam param) {
        return CommonResult.success(CommonPage.restPage(caseDataService.list(param)));
    }
    
    @Operation(summary = "获取案例详情")
    @GetMapping("/{id}")
    public CommonResult<CaseDataResult> getById(@PathVariable Long id) {
        CaseDataResult result = caseDataService.getById(id);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "修改案例状态")
    @PostMapping("/update/status/{id}")
    public CommonResult updateStatus(@PathVariable Long id,
                                    @RequestParam Integer status) {
        int count = caseDataService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "修改显示状态")
    @PostMapping("/update/showStatus/{id}")
    public CommonResult updateShowStatus(@PathVariable Long id,
                                        @RequestParam Integer showStatus) {
        int count = caseDataService.updateShowStatus(id, showStatus);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "更新排序")
    @PostMapping("/update/sort/{id}")
    public CommonResult updateSort(@PathVariable Long id,
                                  @RequestParam Integer sort) {
        int count = caseDataService.updateSort(id, sort);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "批量更新热度分数")
    @PostMapping("/updateHotScore/batch")
    public CommonResult batchUpdateHotScore() {
        int count = caseDataService.batchUpdateHotScore();
        return CommonResult.success(count);
    }
    
    @Operation(summary = "更新指定案例热度分数")
    @PostMapping("/updateHotScore/{id}")
    public CommonResult updateHotScore(@PathVariable Long id,
                                      @RequestParam BigDecimal hotScore) {
        int count = caseDataService.updateHotScore(id, hotScore);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
```

## 验证步骤
1. 启动mall-admin应用
2. 访问Swagger文档测试API接口
3. 测试案例数据的增删改查功能
4. 验证热度分数计算是否正确
5. 测试批量操作功能

## API接口说明
| 接口路径 | 方法 | 功能描述 |
|---------|------|---------|
| `/case/data/create` | POST | 创建案例 |
| `/case/data/update/{id}` | POST | 修改案例 |
| `/case/data/delete/{id}` | POST | 删除案例 |
| `/case/data/delete/batch` | POST | 批量删除案例 |
| `/case/data/list` | GET | 分页查询案例 |
| `/case/data/{id}` | GET | 获取案例详情 |
| `/case/data/update/status/{id}` | POST | 修改案例状态 |
| `/case/data/update/showStatus/{id}` | POST | 修改显示状态 |
| `/case/data/update/sort/{id}` | POST | 更新排序 |
| `/case/data/updateHotScore/batch` | POST | 批量更新热度分数 |
| `/case/data/updateHotScore/{id}` | POST | 更新指定案例热度分数 |

## 输出物
- CaseDataService接口及实现类
- CaseDataController控制器
- 完整的案例数据管理API
- 热度分数计算算法
- Swagger API文档

## 后续任务
- 下一步：06-用户端案例查询API.md