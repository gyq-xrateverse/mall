# 04-3-2: RTK Query API集成

## 任务概述
**时间估算**: 30-45分钟  
**优先级**: 最高  
**依赖关系**: 04-3-1-套餐状态切片完成

## 详细任务清单

### 核心任务
1. **创建subscriptionApi RTK Query**
   - 配置baseQuery和认证头处理
   - 定义tagTypes用于缓存管理
   - 设置自动重新验证机制
   - 配置错误处理和重试策略

2. **实现查询endpoints**
   - getCurrentSubscription: 获取当前订阅
   - getAvailablePlans: 获取可用套餐
   - getPlanComparison: 获取套餐比较
   - getUsageStatistics: 获取使用统计
   - checkPermissions: 权限检查查询

3. **实现变更endpoints**  
   - subscribeToPlan: 订阅套餐变更
   - cancelSubscription: 取消订阅变更
   - renewSubscription: 续费订阅变更
   - upgradeSubscription: 升级套餐变更

4. **配置缓存和失效策略**
   - 设置providesTags和invalidatesTags
   - 优化查询缓存策略
   - 实现智能缓存失效
   - 配置数据同步机制

### 技术要点
- RTK Query API设计最佳实践
- 自动缓存和数据同步
- Token认证和请求拦截
- 类型安全的API调用

## 验收标准
- [ ] RTK Query API定义完整准确
- [ ] 自动缓存和失效机制正常
- [ ] 类型安全的API调用实现
- [ ] 错误处理和重试机制完善
- [ ] 认证和权限验证正确

## 交付物
- [ ] subscriptionApi.ts RTK Query文件
- [ ] 查询和变更hooks导出
- [ ] API调用类型定义
- [ ] 缓存策略配置
- [ ] API集成测试用例

## 技术要点
- **自动缓存**: providesTags和invalidatesTags使用
- **类型安全**: TypeScript泛型和类型推导
- **认证处理**: prepareHeaders自动添加token
- **错误处理**: 统一的错误响应处理

## 下一步
完成后进入 `04-3-3-自定义Hooks`