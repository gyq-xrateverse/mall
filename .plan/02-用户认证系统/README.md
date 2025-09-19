# 02-用户认证系统开发计划 - 总览

## 阶段概述
**总时间**: 1.5周 (7个工作日)  
**优先级**: 最高  
**目标**: 实现完整的多渠道用户认证体系
**依赖**: 01-前端基础架构完成

## 子计划索引

### [02-1: 后端认证API开发](./02-1-后端认证API开发.md)
**时间**: 第1-2天  
**内容**: 扩展mall-portal模块，实现用户认证API
- [x] 数据库设计扩展
- [x] 邮件服务配置
- [x] 验证码服务实现
- [x] JWT Token服务

### [02-2: 认证控制器开发](./02-2-认证控制器开发.md)
**时间**: 第3天  
**内容**: 开发用户认证相关的REST API控制器
- [x] 邮箱验证码认证控制器
- [x] 第三方登录控制器
- [x] 请求响应DTO设计
- [x] 参数验证和错误处理

### [02-3: 前端认证组件开发](./02-3-前端认证组件开发.md)
**时间**: 第4-5天  
**内容**: 开发前端用户认证相关组件和页面
- [x] 登录页面组件
- [x] 注册页面组件
- [x] 认证状态管理
- [x] 认证相关Hooks

### [02-4: 第三方登录集成](./02-4-第三方登录集成.md)
**时间**: 第6-7天  
**内容**: 集成微信扫码登录和Google OAuth登录
- [x] 微信扫码登录集成
- [x] Google OAuth登录集成
- [x] 第三方登录服务
- [x] 账号绑定逻辑

## 核心功能

### 认证方式
1. **邮箱验证码认证** - 注册/登录/密码重置
2. **微信扫码登录** - 集成微信开放平台
3. **Google OAuth登录** - 集成Google APIs

### 技术特性
- **JWT Token管理** - 访问令牌和刷新令牌
- **邮件服务** - 验证码发送和模板
- **安全机制** - 频率限制和防暴力破解
- **用户状态管理** - Redux状态管理

## 数据库设计

### 新增/扩展表
```sql
-- 扩展用户表
ALTER TABLE ums_member ADD COLUMN wechat_openid VARCHAR(64);
ALTER TABLE ums_member ADD COLUMN google_id VARCHAR(128);
ALTER TABLE ums_member ADD COLUMN register_type TINYINT DEFAULT 1;

-- 验证码记录表
CREATE TABLE ums_verification_codes (...);

-- 第三方登录记录表  
CREATE TABLE ums_third_party_auth (...);
```

## API接口设计

### 认证相关接口
- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/register/email` - 邮箱注册
- `POST /api/auth/login/email` - 邮箱登录
- `POST /api/auth/login/wechat` - 微信登录
- `POST /api/auth/login/google` - Google登录
- `POST /api/auth/refresh-token` - 刷新令牌
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/me` - 获取当前用户

## 验收标准

### 功能完整性
- [x] 多种认证方式正常工作
- [x] 用户注册登录流程完整
- [x] Token管理机制正确
- [x] 第三方登录集成成功

### 安全性
- [x] 验证码频率限制
- [x] JWT安全配置
- [x] 密码加密存储
- [x] 输入参数验证

### 用户体验
- [x] 界面友好易用
- [x] 错误提示清晰
- [x] 加载状态处理
- [x] 响应式适配

## 后续接入
完成后可以开始：
1. **首页界面开发**
2. **积分系统开发**
3. **其他业务功能开发**