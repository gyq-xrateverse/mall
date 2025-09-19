# 06-用户端案例查询API

## 任务概述
在mall-portal模块中实现用户端的案例查询接口，支持分类筛选、懒加载分页、点赞、浏览量统计等功能。

## 前置条件
- 案例数据管理API已完成
- mall-portal模块正常运行
- Redis配置可用

## 实施步骤

### 1. 创建Service接口
**文件路径：** `mall-portal/src/main/java/com/macro/mall/portal/service/CaseService.java`

```java
package com.macro.mall.portal.service;

import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.dto.CaseItemResult;
import com.macro.mall.portal.dto.CaseCategoryResult;

import java.util.List;

/**
 * 用户端案例服务
 */
public interface CaseService {
    
    /**
     * 获取完整分类列表（包含虚拟分类）
     */
    List<CaseCategoryResult> getAllCategories();
    
    /**
     * 根据分类类型获取案例列表
     */
    CaseListResult getCaseList(CaseListQueryParam param);
    
    /**
     * 获取案例详情
     */
    CaseItemResult getCaseDetail(Long id);
    
    /**
     * 增加浏览量
     */
    void incrementView(Long id);
    
    /**
     * 切换点赞状态
     */
    boolean toggleLike(Long id, String userKey);
    
    /**
     * 获取用户点赞状态
     */
    boolean isLiked(Long id, String userKey);
}
```

### 2. 实现Service类
**文件路径：** `mall-portal/src/main/java/com/macro/mall/portal/service/impl/CaseServiceImpl.java`

```java
package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.CaseCategoryMapper;
import com.macro.mall.mapper.CaseDataMapper;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseCategoryExample;
import com.macro.mall.model.CaseData;
import com.macro.mall.model.CaseDataExample;
import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.dto.CaseItemResult;
import com.macro.mall.portal.dto.CaseCategoryResult;
import com.macro.mall.portal.service.CaseService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CaseServiceImpl implements CaseService {
    
    @Autowired
    private CaseDataMapper caseDataMapper;
    
    @Autowired
    private CaseCategoryMapper caseCategoryMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String REDIS_PREFIX = "mall:case:";
    private static final String LIKE_KEY_PREFIX = "mall:case:like:";
    
    @Override
    public List<CaseCategoryResult> getAllCategories() {
        String cacheKey = REDIS_PREFIX + "categories:all";
        
        // 尝试从缓存获取
        List<CaseCategoryResult> cached = (List<CaseCategoryResult>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        List<CaseCategoryResult> result = new ArrayList<>();
        
        // 添加虚拟分类
        result.add(createVirtualCategory(0L, "全部", "all"));
        result.add(createVirtualCategory(-1L, "最新", "latest"));
        result.add(createVirtualCategory(-2L, "热门", "hot"));
        
        // 获取真实分类
        CaseCategoryExample example = new CaseCategoryExample();
        example.createCriteria()
                .andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andDeleteStatusEqualTo(0);
        example.setOrderByClause("sort ASC, create_time DESC");
        
        List<CaseCategory> categories = caseCategoryMapper.selectByExample(example);
        for (CaseCategory category : categories) {
            CaseCategoryResult categoryResult = new CaseCategoryResult();
            BeanUtils.copyProperties(category, categoryResult);
            categoryResult.setType("category");
            
            // 统计分类下的案例数量
            int count = countCasesByCategory(category.getId());
            categoryResult.setCount(count);
            
            result.add(categoryResult);
        }
        
        // 缓存30分钟
        redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
        
        return result;
    }
    
    @Override
    public CaseListResult getCaseList(CaseListQueryParam param) {
        String cacheKey = buildCacheKey(param);
        
        // 只对第一页进行缓存（热点数据）
        if (param.getPage() == 1) {
            CaseListResult cached = (CaseListResult) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }
        
        PageHelper.startPage(param.getPage(), param.getSize());
        
        CaseDataExample example = new CaseDataExample();
        CaseDataExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andDeleteStatusEqualTo(0);
        
        // 根据分类类型设置查询条件和排序
        switch (param.getCategoryType()) {
            case "all":
                // 全部：按排序字段和创建时间排序
                example.setOrderByClause("sort ASC, create_time DESC");
                break;
            case "latest":
                // 最新：按创建时间倒序
                example.setOrderByClause("create_time DESC");
                break;
            case "hot":
                // 热门：按热度分数倒序
                example.setOrderByClause("hot_score DESC, create_time DESC");
                break;
            case "category":
                // 指定分类：按分类ID过滤
                if (param.getCategoryId() != null) {
                    criteria.andCategoryIdEqualTo(param.getCategoryId());
                }
                example.setOrderByClause("sort ASC, create_time DESC");
                break;
        }
        
        List<CaseData> caseList = caseDataMapper.selectByExample(example);
        PageInfo<CaseData> pageInfo = new PageInfo<>(caseList);
        
        // 转换为结果对象
        List<CaseItemResult> resultList = caseList.stream()
                .map(this::convertToCaseItemResult)
                .collect(Collectors.toList());
        
        CaseListResult result = new CaseListResult();
        result.setList(resultList);
        result.setTotal(pageInfo.getTotal());
        result.setCurrentPage(pageInfo.getPageNum());
        result.setHasMore(pageInfo.isHasNextPage());
        
        // 缓存第一页数据15分钟
        if (param.getPage() == 1) {
            redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
        }
        
        return result;
    }
    
    @Override
    public CaseItemResult getCaseDetail(Long id) {
        String cacheKey = REDIS_PREFIX + "detail:" + id;
        
        // 尝试从缓存获取
        CaseItemResult cached = (CaseItemResult) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        CaseData caseData = caseDataMapper.selectByPrimaryKey(id);
        if (caseData == null || caseData.getDeleteStatus() == 1 
            || caseData.getStatus() == 0 || caseData.getShowStatus() == 0) {
            return null;
        }
        
        CaseItemResult result = convertToCaseItemResult(caseData);
        
        // 缓存1小时
        redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        
        return result;
    }
    
    @Override
    public void incrementView(Long id) {
        // 更新数据库
        CaseData caseData = new CaseData();
        caseData.setId(id);
        caseDataMapper.incrementViewCount(id); // 需要在Mapper中添加此方法
        
        // 清除相关缓存
        clearRelatedCache(id);
    }
    
    @Override
    public boolean toggleLike(Long id, String userKey) {
        String likeKey = LIKE_KEY_PREFIX + userKey + ":" + id;
        
        boolean isLiked = Boolean.TRUE.equals(redisTemplate.hasKey(likeKey));
        
        if (isLiked) {
            // 取消点赞
            redisTemplate.delete(likeKey);
            caseDataMapper.decrementLikeCount(id); // 需要在Mapper中添加此方法
        } else {
            // 点赞
            redisTemplate.opsForValue().set(likeKey, "1", 30, TimeUnit.DAYS);
            caseDataMapper.incrementLikeCount(id); // 需要在Mapper中添加此方法
        }
        
        // 清除相关缓存
        clearRelatedCache(id);
        
        return !isLiked;
    }
    
    @Override
    public boolean isLiked(Long id, String userKey) {
        String likeKey = LIKE_KEY_PREFIX + userKey + ":" + id;
        return Boolean.TRUE.equals(redisTemplate.hasKey(likeKey));
    }
    
    /**
     * 创建虚拟分类
     */
    private CaseCategoryResult createVirtualCategory(Long id, String name, String type) {
        CaseCategoryResult category = new CaseCategoryResult();
        category.setId(id);
        category.setName(name);
        category.setType(type);
        
        // 统计对应的案例数量
        int count = countCasesByType(type);
        category.setCount(count);
        
        return category;
    }
    
    /**
     * 统计分类下的案例数量
     */
    private int countCasesByCategory(Long categoryId) {
        CaseDataExample example = new CaseDataExample();
        example.createCriteria()
                .andCategoryIdEqualTo(categoryId)
                .andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andDeleteStatusEqualTo(0);
        return (int) caseDataMapper.countByExample(example);
    }
    
    /**
     * 统计虚拟分类下的案例数量
     */
    private int countCasesByType(String type) {
        CaseDataExample example = new CaseDataExample();
        example.createCriteria()
                .andStatusEqualTo(1)
                .andShowStatusEqualTo(1)
                .andDeleteStatusEqualTo(0);
        return (int) caseDataMapper.countByExample(example);
    }
    
    /**
     * 转换为案例项结果
     */
    private CaseItemResult convertToCaseItemResult(CaseData caseData) {
        CaseItemResult result = new CaseItemResult();
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
     * 构建缓存键
     */
    private String buildCacheKey(CaseListQueryParam param) {
        StringBuilder sb = new StringBuilder(REDIS_PREFIX);
        sb.append(param.getCategoryType()).append(":");
        if (param.getCategoryId() != null) {
            sb.append(param.getCategoryId()).append(":");
        }
        sb.append("page:").append(param.getPage());
        return sb.toString();
    }
    
    /**
     * 清除相关缓存
     */
    private void clearRelatedCache(Long caseId) {
        // 清除详情缓存
        redisTemplate.delete(REDIS_PREFIX + "detail:" + caseId);
        
        // 清除列表缓存（模糊匹配删除）
        redisTemplate.delete(redisTemplate.keys(REDIS_PREFIX + "*:page:1"));
        
        // 清除分类缓存
        redisTemplate.delete(REDIS_PREFIX + "categories:all");
    }
}
```

### 3. 扩展CaseDataMapper
**文件路径：** 在`mall-mbg`的CaseDataMapper中添加自定义方法

```java
// 在CaseDataMapper.java中添加方法
/**
 * 增加浏览量
 */
int incrementViewCount(@Param("id") Long id);

/**
 * 增加点赞数
 */
int incrementLikeCount(@Param("id") Long id);

/**
 * 减少点赞数
 */
int decrementLikeCount(@Param("id") Long id);
```

**在CaseDataMapper.xml中添加对应SQL：**

```xml
<update id="incrementViewCount">
    UPDATE case_data 
    SET view_count = view_count + 1 
    WHERE id = #{id}
</update>

<update id="incrementLikeCount">
    UPDATE case_data 
    SET like_count = like_count + 1 
    WHERE id = #{id}
</update>

<update id="decrementLikeCount">
    UPDATE case_data 
    SET like_count = like_count - 1 
    WHERE id = #{id} AND like_count > 0
</update>
```

### 4. 创建Controller
**文件路径：** `mall-portal/src/main/java/com/macro/mall/portal/controller/CaseController.java`

```java
package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.dto.CaseListQueryParam;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.dto.CaseItemResult;
import com.macro.mall.portal.dto.CaseCategoryResult;
import com.macro.mall.portal.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户端案例查询Controller
 */
@RestController
@RequestMapping("/case")
@Tag(name = "CaseController", description = "用户端案例查询")
public class CaseController {
    
    @Autowired
    private CaseService caseService;
    
    @Operation(summary = "获取完整分类列表")
    @GetMapping("/categories/all")
    public CommonResult<List<CaseCategoryResult>> getAllCategories() {
        List<CaseCategoryResult> categories = caseService.getAllCategories();
        return CommonResult.success(categories);
    }
    
    @Operation(summary = "获取案例列表")
    @GetMapping("/list")
    public CommonResult<CaseListResult> getCaseList(CaseListQueryParam param) {
        CaseListResult result = caseService.getCaseList(param);
        return CommonResult.success(result);
    }
    
    @Operation(summary = "获取案例详情")
    @GetMapping("/detail/{id}")
    public CommonResult<CaseItemResult> getCaseDetail(@PathVariable Long id) {
        CaseItemResult result = caseService.getCaseDetail(id);
        if (result != null) {
            return CommonResult.success(result);
        }
        return CommonResult.failed("案例不存在");
    }
    
    @Operation(summary = "增加浏览量")
    @PostMapping("/view/{id}")
    public CommonResult incrementView(@PathVariable Long id) {
        caseService.incrementView(id);
        return CommonResult.success("浏览量已更新");
    }
    
    @Operation(summary = "切换点赞状态")
    @PostMapping("/like/{id}")
    public CommonResult<Boolean> toggleLike(@PathVariable Long id, HttpServletRequest request) {
        // 使用IP作为用户标识（实际项目中可能使用用户ID或设备ID）
        String userKey = getClientIP(request);
        boolean isLiked = caseService.toggleLike(id, userKey);
        return CommonResult.success(isLiked);
    }
    
    @Operation(summary = "获取点赞状态")
    @GetMapping("/like/status/{id}")
    public CommonResult<Boolean> getLikeStatus(@PathVariable Long id, HttpServletRequest request) {
        String userKey = getClientIP(request);
        boolean isLiked = caseService.isLiked(id, userKey);
        return CommonResult.success(isLiked);
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
```

## 验证步骤
1. 启动mall-portal应用
2. 测试分类列表接口
3. 测试不同分类类型的案例列表查询
4. 验证分页功能
5. 测试点赞和浏览量功能
6. 检查缓存是否正常工作

## API接口说明
| 接口路径 | 方法 | 功能描述 |
|---------|------|---------|
| `/case/categories/all` | GET | 获取完整分类列表 |
| `/case/list` | GET | 获取案例列表 |
| `/case/detail/{id}` | GET | 获取案例详情 |
| `/case/view/{id}` | POST | 增加浏览量 |
| `/case/like/{id}` | POST | 切换点赞状态 |
| `/case/like/status/{id}` | GET | 获取点赞状态 |

## 输出物
- CaseService接口及实现类
- CaseController控制器
- 用户端案例查询API
- Redis缓存集成
- 点赞和浏览量统计功能

## 后续任务
- 下一步：07-热度计算定时任务.md