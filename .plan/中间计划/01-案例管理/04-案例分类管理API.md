# 04-案例分类管理API

## 任务概述
在mall-admin模块中实现案例分类的管理接口，包括增删改查、状态管理、排序等功能。

## 前置条件
- 实体类和Mapper已生成
- DTO类已创建完成
- mall-admin模块正常运行

## 实施步骤

### 1. 创建Service接口
**文件路径：** `mall-admin/src/main/java/com/macro/mall/service/CaseCategoryService.java`

```java
package com.macro.mall.service;

import com.macro.mall.dto.CaseCategoryParam;
import com.macro.mall.dto.CaseCategoryQueryParam;
import com.macro.mall.model.CaseCategory;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * 案例分类管理Service
 */
public interface CaseCategoryService {
    
    /**
     * 创建分类
     */
    int create(CaseCategoryParam param);
    
    /**
     * 修改分类
     */
    int update(Long id, CaseCategoryParam param);
    
    /**
     * 删除分类
     */
    int delete(Long id);
    
    /**
     * 批量删除分类
     */
    int delete(List<Long> ids);
    
    /**
     * 分页查询分类
     */
    PageInfo<CaseCategory> list(CaseCategoryQueryParam param);
    
    /**
     * 获取分类详情
     */
    CaseCategory getById(Long id);
    
    /**
     * 获取所有启用的分类
     */
    List<CaseCategory> listAll();
    
    /**
     * 修改分类状态
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
}
```

### 2. 实现Service类
**文件路径：** `mall-admin/src/main/java/com/macro/mall/service/impl/CaseCategoryServiceImpl.java`

```java
package com.macro.mall.service.impl;

import com.macro.mall.dto.CaseCategoryParam;
import com.macro.mall.dto.CaseCategoryQueryParam;
import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseCategoryExample;
import com.macro.mall.service.CaseCategoryService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 案例分类管理Service实现类
 */
@Service
public class CaseCategoryServiceImpl implements CaseCategoryService {
    
    @Autowired
    private CaseCategoryMapper caseCategoryMapper;
    
    @Override
    public int create(CaseCategoryParam param) {
        CaseCategory category = new CaseCategory();
        BeanUtils.copyProperties(param, category);
        category.setCreateTime(new Date());
        category.setDeleteStatus(0);
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getShowStatus() == null) {
            category.setShowStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        return caseCategoryMapper.insert(category);
    }
    
    @Override
    public int update(Long id, CaseCategoryParam param) {
        CaseCategory category = new CaseCategory();
        BeanUtils.copyProperties(param, category);
        category.setId(id);
        category.setModifyTime(new Date());
        return caseCategoryMapper.updateByPrimaryKeySelective(category);
    }
    
    @Override
    public int delete(Long id) {
        // 软删除
        CaseCategory category = new CaseCategory();
        category.setId(id);
        category.setDeleteStatus(1);
        category.setModifyTime(new Date());
        return caseCategoryMapper.updateByPrimaryKeySelective(category);
    }
    
    @Override
    public int delete(List<Long> ids) {
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria().andIdIn(ids);
        CaseCategory category = new CaseCategory();
        category.setDeleteStatus(1);
        category.setModifyTime(new Date());
        return caseCategoryMapper.updateByExampleSelective(category, example);
    }
    
    @Override
    public PageInfo<CaseCategory> list(CaseCategoryQueryParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        CaseCategoryExample example = new CaseCategoryExample();
        CaseCategoryExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        
        if (StringUtils.hasText(param.getName())) {
            criteria.andNameLike("%" + param.getName() + "%");
        }
        if (param.getStatus() != null) {
            criteria.andStatusEqualTo(param.getStatus());
        }
        
        example.setOrderByClause("sort ASC, create_time DESC");
        List<CaseCategory> list = caseCategoryMapper.selectByExample(example);
        return new PageInfo<>(list);
    }
    
    @Override
    public CaseCategory getById(Long id) {
        return caseCategoryMapper.selectByPrimaryKey(id);
    }
    
    @Override
    public List<CaseCategory> listAll() {
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria()
                .andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andDeleteStatusEqualTo(0);
        example.setOrderByClause("sort ASC, create_time DESC");
        return caseCategoryMapper.selectByExample(example);
    }
    
    @Override
    public int updateStatus(Long id, Integer status) {
        CaseCategory category = new CaseCategory();
        category.setId(id);
        category.setStatus(status);
        category.setModifyTime(new Date());
        return caseCategoryMapper.updateByPrimaryKeySelective(category);
    }
    
    @Override
    public int updateShowStatus(Long id, Integer showStatus) {
        CaseCategory category = new CaseCategory();
        category.setId(id);
        category.setShowStatus(showStatus);
        category.setModifyTime(new Date());
        return caseCategoryMapper.updateByPrimaryKeySelective(category);
    }
    
    @Override
    public int updateSort(Long id, Integer sort) {
        CaseCategory category = new CaseCategory();
        category.setId(id);
        category.setSort(sort);
        category.setModifyTime(new Date());
        return caseCategoryMapper.updateByPrimaryKeySelective(category);
    }
}
```

### 3. 创建Controller
**文件路径：** `mall-admin/src/main/java/com/macro/mall/controller/CaseCategoryController.java`

```java
package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.CaseCategoryParam;
import com.macro.mall.dto.CaseCategoryQueryParam;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.service.CaseCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 案例分类管理Controller
 */
@RestController
@RequestMapping("/case/category")
@Tag(name = "CaseCategoryController", description = "案例分类管理")
public class CaseCategoryController {
    
    @Autowired
    private CaseCategoryService caseCategoryService;
    
    @Operation(summary = "创建案例分类")
    @PostMapping("/create")
    public CommonResult create(@Validated @RequestBody CaseCategoryParam param) {
        int count = caseCategoryService.create(param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "修改案例分类")
    @PostMapping("/update/{id}")
    public CommonResult update(@PathVariable Long id,
                              @Validated @RequestBody CaseCategoryParam param) {
        int count = caseCategoryService.update(id, param);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "删除案例分类")
    @PostMapping("/delete/{id}")
    public CommonResult delete(@PathVariable Long id) {
        int count = caseCategoryService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "批量删除案例分类")
    @PostMapping("/delete/batch")
    public CommonResult deleteBatch(@RequestParam("ids") List<Long> ids) {
        int count = caseCategoryService.delete(ids);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "分页查询案例分类")
    @GetMapping("/list")
    public CommonResult<CommonPage<CaseCategory>> list(CaseCategoryQueryParam param) {
        return CommonResult.success(CommonPage.restPage(caseCategoryService.list(param)));
    }
    
    @Operation(summary = "获取案例分类详情")
    @GetMapping("/{id}")
    public CommonResult<CaseCategory> getById(@PathVariable Long id) {
        CaseCategory category = caseCategoryService.getById(id);
        return CommonResult.success(category);
    }
    
    @Operation(summary = "获取所有启用的案例分类")
    @GetMapping("/listAll")
    public CommonResult<List<CaseCategory>> listAll() {
        List<CaseCategory> list = caseCategoryService.listAll();
        return CommonResult.success(list);
    }
    
    @Operation(summary = "修改分类状态")
    @PostMapping("/update/status/{id}")
    public CommonResult updateStatus(@PathVariable Long id,
                                    @RequestParam Integer status) {
        int count = caseCategoryService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "修改显示状态")
    @PostMapping("/update/showStatus/{id}")
    public CommonResult updateShowStatus(@PathVariable Long id,
                                        @RequestParam Integer showStatus) {
        int count = caseCategoryService.updateShowStatus(id, showStatus);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
    
    @Operation(summary = "更新排序")
    @PostMapping("/update/sort/{id}")
    public CommonResult updateSort(@PathVariable Long id,
                                  @RequestParam Integer sort) {
        int count = caseCategoryService.updateSort(id, sort);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}
```

## 验证步骤
1. 启动mall-admin应用
2. 访问Swagger文档验证API接口
3. 测试各个接口功能：
   - 创建分类
   - 查询分类列表
   - 修改分类信息
   - 删除分类
   - 状态管理

## API接口说明
| 接口路径 | 方法 | 功能描述 |
|---------|------|---------|
| `/case/category/create` | POST | 创建案例分类 |
| `/case/category/update/{id}` | POST | 修改案例分类 |
| `/case/category/delete/{id}` | POST | 删除案例分类 |
| `/case/category/delete/batch` | POST | 批量删除案例分类 |
| `/case/category/list` | GET | 分页查询案例分类 |
| `/case/category/{id}` | GET | 获取分类详情 |
| `/case/category/listAll` | GET | 获取所有启用分类 |
| `/case/category/update/status/{id}` | POST | 修改分类状态 |
| `/case/category/update/showStatus/{id}` | POST | 修改显示状态 |
| `/case/category/update/sort/{id}` | POST | 更新排序 |

## 输出物
- CaseCategoryService接口及实现类
- CaseCategoryController控制器
- 完整的分类管理API接口
- Swagger API文档

## 后续任务
- 下一步：05-案例数据管理API.md