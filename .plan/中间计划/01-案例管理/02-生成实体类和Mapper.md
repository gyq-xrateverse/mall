# 02-生成实体类和Mapper

## 任务概述
使用MyBatis Generator在mall-mbg模块中生成案例相关的实体类和Mapper接口。

## 前置条件
- 数据库表已创建完成（case_category、case_data）
- mall-mbg模块配置正常
- MyBatis Generator配置文件存在

## 实施步骤

### 1. 配置MyBatis Generator
找到并修改MyBatis Generator配置文件（通常在`mall-mbg/src/main/resources/generatorConfig.xml`）

### 2. 添加案例表配置
在配置文件中添加案例相关表的生成配置：

```xml
<!-- 案例分类表 -->
<table tableName="case_category" domainObjectName="CaseCategory" enableCountByExample="true"
       enableUpdateByExample="true" enableDeleteByExample="true" enableSelectByExample="true"
       selectByExampleQueryId="true">
    <generatedKey column="id" sqlStatement="MySql" identity="true"/>
</table>

<!-- 案例数据表 -->
<table tableName="case_data" domainObjectName="CaseData" enableCountByExample="true"
       enableUpdateByExample="true" enableDeleteByExample="true" enableSelectByExample="true"
       selectByExampleQueryId="true">
    <generatedKey column="id" sqlStatement="MySql" identity="true"/>
</table>
```

### 3. 运行代码生成
执行MyBatis Generator生成代码（根据项目配置方式）：

#### 方式一：Maven插件
```bash
cd /mnt/d/software/beilv-agent/mall/mall/mall-mbg
mvn mybatis-generator:generate
```

#### 方式二：Java程序
运行Generator主类（如果项目中有的话）

### 4. 验证生成的文件
检查以下文件是否生成成功：

#### 实体类（Model）
- `src/main/java/com/macro/mall/model/CaseCategory.java`
- `src/main/java/com/macro/mall/model/CaseCategoryExample.java`
- `src/main/java/com/macro/mall/model/CaseData.java`
- `src/main/java/com/macro/mall/model/CaseDataExample.java`

#### Mapper接口
- `src/main/java/com/macro/mall/mapper/CaseCategoryMapper.java`
- `src/main/java/com/macro/mall/mapper/CaseDataMapper.java`

#### XML映射文件
- `src/main/resources/com/macro/mall/mapper/CaseCategoryMapper.xml`
- `src/main/resources/com/macro/mall/mapper/CaseDataMapper.xml`

### 5. 检查生成的代码
确认生成的实体类包含所有必要的字段：

#### CaseCategory实体类应包含：
- id, name, sort
- status, showStatus, deleteStatus  
- createTime, modifyTime
- createBy, modifyBy

#### CaseData实体类应包含：
- id, categoryId, title, description
- previewImageUrl, videoUrl, videoPreviewUrl
- likeCount, viewCount, hotScore, hotUpdateTime
- sort, status, showStatus, deleteStatus
- createTime, modifyTime, createBy, modifyBy

### 6. 自定义Mapper方法
在Mapper接口中添加业务需要的自定义方法（如果需要）：

#### CaseDataMapper添加方法：
```java
// 按热度查询
List<CaseData> selectByHotScore(@Param("limit") int limit);

// 按分类和热度查询
List<CaseData> selectByCategoryAndHotScore(@Param("categoryId") Long categoryId, @Param("limit") int limit);

// 按最新时间查询
List<CaseData> selectByLatest(@Param("limit") int limit);

// 更新热度分数
int updateHotScore(@Param("id") Long id, @Param("hotScore") BigDecimal hotScore);
```

### 7. 编写自定义SQL
在对应的XML文件中实现自定义方法：

```xml
<!-- 按热度查询 -->
<select id="selectByHotScore" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM case_data 
    WHERE status = 1 AND show_status = 1 AND delete_status = 0
    ORDER BY hot_score DESC 
    LIMIT #{limit}
</select>

<!-- 按分类和热度查询 -->
<select id="selectByCategoryAndHotScore" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM case_data 
    WHERE category_id = #{categoryId} 
      AND status = 1 AND show_status = 1 AND delete_status = 0
    ORDER BY hot_score DESC 
    LIMIT #{limit}
</select>

<!-- 按最新时间查询 -->
<select id="selectByLatest" resultMap="BaseResultMap">
    SELECT <include refid="Base_Column_List"/>
    FROM case_data 
    WHERE status = 1 AND show_status = 1 AND delete_status = 0
    ORDER BY create_time DESC 
    LIMIT #{limit}
</select>

<!-- 更新热度分数 -->
<update id="updateHotScore">
    UPDATE case_data 
    SET hot_score = #{hotScore}, hot_update_time = NOW()
    WHERE id = #{id}
</update>
```

## 验证步骤
1. 编译mall-mbg模块确认无语法错误
2. 编写简单测试验证Mapper接口可用
3. 测试自定义查询方法是否正常

## 注意事项
1. 生成的代码不要手动修改，如需自定义使用继承或组合
2. 自定义SQL写在XML文件末尾，避免重新生成时丢失
3. 检查字段映射是否正确，特别是驼峰命名转换

## 输出物
- CaseCategory和CaseData实体类
- 对应的Mapper接口和XML映射文件
- 自定义查询方法
- 验证测试代码

## 后续任务
- 下一步：03-创建DTO类.md