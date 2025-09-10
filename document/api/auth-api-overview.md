# BEILV AI Mall Portal 认证API接口文档

## 概述
本文档描述了BEILV AI商城门户认证系统的REST API接口，包括用户注册、登录、验证码、密码重置等功能。

## 基础信息
- **服务名称**: mall-portal
- **版本**: 1.0.0
- **基础URL**: http://localhost:8085/api
- **Content-Type**: application/json
- **字符编码**: UTF-8

## 接口列表

### 1. 测试接口 (/api/test)
- `GET /api/test/health` - 健康检查
- `GET /api/test/info` - API信息

### 2. 认证接口 (/api/auth)
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/send-code` - 发送验证码
- `POST /api/auth/reset-password` - 重置密码
- `POST /api/auth/refresh-token` - 刷新Token
- `GET /api/auth/user-info` - 获取用户信息
- `POST /api/auth/logout` - 用户注销
- `GET /api/auth/check-email` - 检查邮箱是否存在
- `GET /api/auth/check-username` - 检查用户名是否存在

## 响应格式
所有API响应都采用统一的格式：

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        // 具体数据内容
    }
}
```

### 状态码说明
- `200` - 成功
- `400` - 参数错误
- `401` - 未授权
- `500` - 服务器内部错误

## 认证方式
- **无认证接口**: 注册、登录、发送验证码、重置密码等
- **需要认证接口**: 获取用户信息、注销等
- **认证方式**: Bearer Token
- **请求头**: `Authorization: Bearer {access_token}`

## 主要功能

### 用户注册流程
1. 发送验证码到邮箱
2. 提交注册信息（包含验证码）
3. 返回access_token和用户信息

### 用户登录流程
1. 提交邮箱和密码
2. 验证成功后返回access_token和用户信息

### 密码重置流程
1. 发送重置密码验证码到邮箱
2. 提交新密码和验证码
3. 密码重置成功

## Swagger文档
访问 http://localhost:8085/swagger-ui.html 查看完整的交互式API文档。

## 开发者信息
- **开发者**: Claude
- **开发时间**: 2025-09-10
- **联系方式**: 通过系统管理员联系