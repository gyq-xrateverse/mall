# 03-创建DTO类

## 任务概述
在mall-admin和mall-portal模块中创建案例相关的DTO（数据传输对象）类，用于API接口的参数传递和结果返回。

## 前置条件
- 实体类和Mapper已生成完成
- 了解现有DTO类的命名规范和结构

## 实施步骤

### 1. mall-admin模块DTO类

#### 创建案例分类相关DTO
**文件路径：** `mall-admin/src/main/java/com/macro/mall/dto/`

##### CaseCategoryParam.java
```java
package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

/**
 * 案例分类参数
 */
@Schema(description = "案例分类参数")
public class CaseCategoryParam {
    
    @Schema(title = "分类ID")
    private Long id;
    
    @NotEmpty(message = "分类名称不能为空")
    @Schema(title = "分类名称", required = true)
    private String name;
    
    @Schema(title = "排序字段")
    private Integer sort;
    
    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus;
    
    // getter和setter方法
}
```

##### CaseCategoryQueryParam.java
```java
package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例分类查询参数
 */
@Schema(description = "案例分类查询参数")
public class CaseCategoryQueryParam {
    
    @Schema(title = "分类名称")
    private String name;
    
    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "页码", defaultValue = "1")
    private Integer pageNum = 1;
    
    @Schema(title = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;
    
    // getter和setter方法
}
```

#### 创建案例数据相关DTO

##### CaseDataParam.java
```java
package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 案例数据参数
 */
@Schema(description = "案例数据参数")
public class CaseDataParam {
    
    @Schema(title = "案例ID")
    private Long id;
    
    @NotNull(message = "分类ID不能为空")
    @Schema(title = "分类ID", required = true)
    private Long categoryId;
    
    @NotEmpty(message = "案例标题不能为空")
    @Schema(title = "案例标题", required = true)
    private String title;
    
    @Schema(title = "案例描述")
    private String description;
    
    @Schema(title = "预览图URL")
    private String previewImageUrl;
    
    @Schema(title = "视频URL")
    private String videoUrl;
    
    @Schema(title = "视频预览地址")
    private String videoPreviewUrl;
    
    @Schema(title = "点赞数")
    private Integer likeCount;
    
    @Schema(title = "浏览次数")
    private Integer viewCount;
    
    @Schema(title = "热度分数")
    private BigDecimal hotScore;
    
    @Schema(title = "排序字段")
    private Integer sort;
    
    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus;
    
    // getter和setter方法
}
```

##### CaseDataQueryParam.java
```java
package com.macro.mall.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例数据查询参数
 */
@Schema(description = "案例数据查询参数")
public class CaseDataQueryParam {
    
    @Schema(title = "分类ID")
    private Long categoryId;
    
    @Schema(title = "案例标题")
    private String title;
    
    @Schema(title = "状态：0->禁用；1->启用")
    private Integer status;
    
    @Schema(title = "显示状态：0->不显示；1->显示")
    private Integer showStatus;
    
    @Schema(title = "排序字段：create_time,hot_score,view_count")
    private String sortField;
    
    @Schema(title = "排序方向：asc,desc")
    private String sortOrder = "desc";
    
    @Schema(title = "页码", defaultValue = "1")
    private Integer pageNum = 1;
    
    @Schema(title = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;
    
    // getter和setter方法
}
```

##### CaseDataResult.java
```java
package com.macro.mall.dto;

import com.macro.mall.model.CaseData;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例数据结果
 */
@Schema(description = "案例数据结果")
public class CaseDataResult extends CaseData {
    
    @Schema(title = "分类名称")
    private String categoryName;
    
    @Schema(title = "创建人昵称")
    private String createByName;
    
    @Schema(title = "修改人昵称")
    private String modifyByName;
    
    // getter和setter方法
}
```

### 2. mall-portal模块DTO类

#### 创建用户端查询参数
**文件路径：** `mall-portal/src/main/java/com/macro/mall/portal/dto/`

##### CaseListQueryParam.java
```java
package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户端案例列表查询参数
 */
@Schema(description = "案例列表查询参数")
public class CaseListQueryParam {
    
    @Schema(title = "分类类型：all,latest,hot,category")
    private String categoryType = "all";
    
    @Schema(title = "分类ID（categoryType为category时需要）")
    private Long categoryId;
    
    @Schema(title = "页码", defaultValue = "1")
    private Integer page = 1;
    
    @Schema(title = "每页数量", defaultValue = "20")
    private Integer size = 20;
    
    // getter和setter方法
}
```

##### CaseListResult.java
```java
package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * 案例列表查询结果
 */
@Schema(description = "案例列表查询结果")
public class CaseListResult {
    
    @Schema(title = "案例列表")
    private List<CaseItemResult> list;
    
    @Schema(title = "总数")
    private Long total;
    
    @Schema(title = "是否还有更多")
    private Boolean hasMore;
    
    @Schema(title = "当前页码")
    private Integer currentPage;
    
    // getter和setter方法
}
```

##### CaseItemResult.java
```java
package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 案例项结果
 */
@Schema(description = "案例项结果")
public class CaseItemResult {
    
    @Schema(title = "案例ID")
    private Long id;
    
    @Schema(title = "分类ID")
    private Long categoryId;
    
    @Schema(title = "分类名称")
    private String categoryName;
    
    @Schema(title = "案例标题")
    private String title;
    
    @Schema(title = "案例描述")
    private String description;
    
    @Schema(title = "预览图URL")
    private String previewImageUrl;
    
    @Schema(title = "视频URL")
    private String videoUrl;
    
    @Schema(title = "视频预览地址")
    private String videoPreviewUrl;
    
    @Schema(title = "点赞数")
    private Integer likeCount;
    
    @Schema(title = "浏览次数")
    private Integer viewCount;
    
    @Schema(title = "热度分数")
    private BigDecimal hotScore;
    
    @Schema(title = "创建时间")
    private Date createTime;
    
    // getter和setter方法
}
```

##### CaseCategoryResult.java
```java
package com.macro.mall.portal.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 案例分类结果
 */
@Schema(description = "案例分类结果")
public class CaseCategoryResult {
    
    @Schema(title = "分类ID")
    private Long id;
    
    @Schema(title = "分类名称")
    private String name;
    
    @Schema(title = "分类类型：all,latest,hot,category")
    private String type;
    
    @Schema(title = "案例数量")
    private Integer count;
    
    // getter和setter方法
}
```

## 验证步骤
1. 编译各模块确认DTO类无语法错误
2. 检查字段命名是否符合驼峰规范
3. 验证注解配置是否正确
4. 测试序列化/反序列化是否正常

## 设计说明
1. **参数验证**：使用Jakarta Validation注解进行参数校验
2. **API文档**：使用Swagger注解生成API文档
3. **继承关系**：Result类继承Entity类减少重复代码
4. **字段映射**：DTO字段与实体类字段保持对应关系

## 输出物
- mall-admin模块的DTO类（Param、QueryParam、Result）
- mall-portal模块的DTO类（QueryParam、Result）
- 参数验证和API文档注解

## 后续任务
- 下一步：04-案例分类管理API.md