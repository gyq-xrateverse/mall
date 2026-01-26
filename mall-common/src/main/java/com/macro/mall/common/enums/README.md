# 积分类型枚举使用说明

## 枚举定义

**位置**: `mall-common/src/main/java/com/macro/mall/common/enums/IntegrationCreditType.java`

### 枚举值

| 枚举值 | Code | 描述 | 说明 |
|--------|------|------|------|
| FREE | 1 | 免费积分 | 有过期时间的积分 |
| PERMANENT | 2 | 永久积分 | 无过期时间的积分（默认） |

## 使用方式

### 1. 基本使用

```java
// 导入枚举类
import com.macro.mall.common.enums.IntegrationCreditType;

// 设置积分类型为永久积分
history.setCreditType(IntegrationCreditType.PERMANENT.getCode());

// 设置积分类型为免费积分
history.setCreditType(IntegrationCreditType.FREE.getCode());
```

### 2. 获取枚举信息

```java
// 获取code值
Integer code = IntegrationCreditType.PERMANENT.getCode(); // 返回 2

// 获取描述信息
String desc = IntegrationCreditType.PERMANENT.getDescription(); // 返回 "永久积分"
```

### 3. 根据code查找枚举

```java
// 根据code获取枚举对象
IntegrationCreditType type = IntegrationCreditType.getByCode(2);
if (type != null) {
    System.out.println(type.getDescription()); // 输出: 永久积分
}

// 验证code是否有效
boolean valid = IntegrationCreditType.isValid(2); // 返回 true
boolean invalid = IntegrationCreditType.isValid(999); // 返回 false
```

### 4. 在Service层使用

```java
@Service
public class UmsMemberIntegrationServiceImpl implements UmsMemberIntegrationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addIntegration(UmsMemberIntegrationParam param) {
        // ... 其他代码 ...

        // 插入历史记录时设置积分类型
        UmsIntegrationChangeHistory history = new UmsIntegrationChangeHistory();
        history.setMemberId(param.getMemberId());
        history.setChangeType(0);
        history.setChangeCount(param.getIntegration());
        history.setSourceType(1);
        history.setCreditType(IntegrationCreditType.PERMANENT.getCode()); // 使用枚举
        historyMapper.insert(history);

        return 1;
    }
}
```

### 5. 在Controller层使用

```java
@RestController
@RequestMapping("/api/integration")
public class IntegrationController {

    @GetMapping("/credit-types")
    public CommonResult<List<Map<String, Object>>> getCreditTypes() {
        List<Map<String, Object>> types = new ArrayList<>();
        for (IntegrationCreditType type : IntegrationCreditType.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", type.getCode());
            map.put("description", type.getDescription());
            types.add(map);
        }
        return CommonResult.success(types);
    }
}
```

## 已应用位置

枚举已在以下6处代码中使用：

| 方法 | 行号 | 操作类型 | 积分类型 |
|------|------|----------|----------|
| addIntegration() | 123行 | 增加积分 | PERMANENT (永久积分) |
| reduceIntegration() | 163行 | 减少积分 | PERMANENT (永久积分) |
| giftIntegration() | 198行 | 赠送积分 | PERMANENT (永久积分) |
| freezeIntegration() | 252行 | 冻结积分 | PERMANENT (永久积分) |
| deductIntegration() | 301行 | 扣减确认 | PERMANENT (永久积分) |
| releaseIntegration() | 352行 | 释放积分 | PERMANENT (永久积分) |

## 优势

1. **类型安全**: 使用枚举可以避免魔法数字，提高代码可读性
2. **维护性**: 修改积分类型定义时，只需修改枚举类即可
3. **可扩展**: 未来可以轻松添加新的积分类型（如临时积分、活动积分等）
4. **验证**: 提供了验证方法，可以检查code值是否有效

## 数据库对应关系

```sql
-- 数据库表定义
CREATE TABLE `ums_integration_change_history` (
  `credit_type` tinyint(4) DEFAULT '2' COMMENT '积分类型: 1-免费积分, 2-永久积分',
  -- ...其他字段
);

-- 枚举映射
IntegrationCreditType.FREE.getCode()       -> 1 (免费积分)
IntegrationCreditType.PERMANENT.getCode()  -> 2 (永久积分)
```

## 未来扩展示例

如需添加新的积分类型，只需在枚举中添加即可：

```java
public enum IntegrationCreditType {
    FREE(1, "免费积分"),
    PERMANENT(2, "永久积分"),
    ACTIVITY(3, "活动积分"),    // 新增：活动积分
    TRIAL(4, "试用积分");       // 新增：试用积分

    // ... 其他代码保持不变
}
```

## 相关文件

- 枚举定义: `mall-common/src/main/java/com/macro/mall/common/enums/IntegrationCreditType.java`
- 使用位置: `mall-integration/src/main/java/com/macro/mall/integration/service/UmsMemberIntegrationServiceImpl.java`
- 数据库表: `ums_integration_change_history`
- SQL定义: `mall/document/sql/mall-20250922.sql`

---

**创建时间**: 2026-01-23
**最后更新**: 2026-01-23
**版本**: 1.0.0
