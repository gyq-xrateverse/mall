# 03-3-2: RTK Query API集成

## 任务概述
**时间估算**: 30-40分钟  
**优先级**: 高  
**依赖关系**: 需要完成积分状态切片(03-3-1)  
**技术栈**: RTK Query, TypeScript, React  
**复杂度**: 中等

## 详细任务清单

### 1. RTK Query API定义
- [ ] 创建creditsApi使用createApi
- [ ] 配置fetchBaseQuery基础查询
- [ ] 设置baseURL和reducerPath
- [ ] 配置JWT token认证头
- [ ] 定义tagTypes缓存标签

### 2. 查询端点定义
- [ ] getCreditsBalance查询积分余额
- [ ] getCreditsHistory查询积分历史
- [ ] getCreditProducts查询积分商品
- [ ] checkCreditsBalance检查余额是否充足
- [ ] 设置适当的缓存标签

### 3. 变更端点定义
- [ ] consumeCredits消费积分变更
- [ ] purchaseCredits购买积分变更
- [ ] 设置缓存失效标签(invalidatesTags)
- [ ] 实现乐观更新机制

### 4. TypeScript类型集成
- [ ] 定义API响应类型
- [ ] 定义查询参数类型
- [ ] 定义变更参数类型
- [ ] 导出类型化的hooks

## 验收标准

### 功能验收
- [ ] 所有API端点定义完整
- [ ] 缓存机制正常工作
- [ ] 自动缓存失效机制正确
- [ ] JWT认证集成正常

### 质量验收
- [ ] TypeScript类型安全完整
- [ ] API调用错误处理完善
- [ ] 代码结构清晰可维护
- [ ] 遵循RTK Query最佳实践

### 性能验收
- [ ] 缓存策略合理高效
- [ ] 自动重试机制正常
- [ ] 网络请求优化
- [ ] 内存使用合理

## 交付物
- [ ] creditsApi.ts API定义文件
- [ ] 相关TypeScript类型定义
- [ ] 类型化的React hooks导出
- [ ] API集成配置文件
- [ ] 单元测试用例

## 技术要点

### RTK Query最佳实践
- 使用createApi创建统一API服务
- 合理设置tagTypes和providesTags
- 正确使用invalidatesTags失效缓存
- 实现类型安全的API调用

### 缓存策略设计
- 积分余额需要实时数据
- 积分历史可适度缓存
- 积分商品可長时间缓存
- 变更操作后自动失效相关缓存

### 错误处理机制
- 统一的错误响应处理
- 网络错误自动重试
- 用户友好的错误提示
- 错误状态管理

### 性能优協
- 合理的请求合并策略
- 防抖和节流优化
- 预取和预加载机制
- 内存泄漏防止

## 下一步
完成后进入 `03-3-3-自定义Hooks` 任务

## 注意事项
1. 确保所有API调用都有适当的缓存策略
2. 注意处理网络请求失败的情况
3. 合理设置缓存超时时间
4. 考虑离线情况下的用户体验
5. 保证认证信息的安全性