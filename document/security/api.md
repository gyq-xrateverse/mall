# Portal模块认证接口详细文档

## 概述

本文档详细描述了Portal模块用户认证相关的API接口，包括请求参数、响应格式、错误码等详细信息。

## 接口列表

### 1. 用户登录

#### 基本信息
- **接口路径**: `POST /api/auth/login`
- **接口描述**: 用户邮箱密码登录，生成access token和refresh token并存储到Redis
- **认证要求**: 无需认证

#### 请求参数

**请求头**
```
Content-Type: application/json
```

**请求体**
```json
{
  "email": "string",      // 必填，用户邮箱
  "password": "string"    // 必填，用户密码
}
```

**参数说明**
| 字段 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| email | string | 是 | 用户注册邮箱，需要符合邮箱格式 | user@example.com |
| password | string | 是 | 用户密码，长度6-20位 | password123 |

#### 响应数据

**成功响应 (200)**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTY5NTU2MTIwMCwiZXhwIjoxNjk1NjQ3NjAwfQ.signature",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6InJlZnJlc2giLCJpYXQiOjE2OTU1NjEyMDAsImV4cCI6MTY5NjE2NjAwMH0.signature",
    "tokenType": "Bearer",
    "expiresIn": "86400",
    "userInfo": {
      "id": 1,
      "username": "user123",
      "email": "user@example.com",
      "nickname": "用户昵称",
      "avatar": "http://example.com/avatar.jpg",
      "status": 1,
      "createTime": "2025-09-24T10:00:00"
    }
  }
}
```

**失败响应 (400/401)**
```json
{
  "code": 400,
  "message": "邮箱或密码错误",
  "data": null
}
```

#### Redis存储

登录成功后会在Redis中存储以下数据：
- **Access Token**: `portal:access_token:{username}:{userId}` → `{accessToken}` (24小时过期)
- **Refresh Token**: `portal:refresh_token:{username}` → `{refreshToken}` (7天过期)

#### 错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 400 | 邮箱不能为空 | 请求参数验证失败 |
| 400 | 密码不能为空 | 请求参数验证失败 |
| 400 | 邮箱格式不正确 | 邮箱格式验证失败 |
| 401 | 邮箱或密码错误 | 用户名密码不匹配 |
| 500 | 登录失败: {具体错误信息} | 服务器内部错误 |

---

### 2. Token验证（网关接口）

#### 基本信息
- **接口路径**: `POST /api/auth/validate-token`
- **接口描述**: 网关调用接口，验证Token的有效性，先检查Redis再验证JWT
- **认证要求**: 无需认证（网关内部调用）

#### 请求参数

**请求头**
```
Content-Type: application/x-www-form-urlencoded
```

**请求参数**
```
token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxfQ.signature
```

**参数说明**
| 字段 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| token | string | 是 | 待验证的JWT access token | eyJhbGciOiJIUzUxMiJ9... |

#### 响应数据

**成功响应 (200)**
```json
{
  "code": 200,
  "message": "Token验证成功",
  "data": {
    "username": "user123",
    "userId": 1,
    "userType": "member",
    "valid": true
  }
}
```

**失败响应 (400/401)**
```json
{
  "code": 400,
  "message": "Token验证失败",
  "data": null
}
```

#### 验证流程

1. 检查token格式是否正确
2. 从token中提取用户信息（username, userId）
3. 检查Redis中是否存在对应的access token
4. 验证JWT签名和过期时间
5. 检查用户类型是否为"member"
6. 检查token类型是否为"access"

#### 错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 400 | Token不能为空 | 请求参数验证失败 |
| 400 | Token验证失败 | Token无效、过期或被注销 |
| 500 | Token验证异常: {具体错误信息} | 服务器内部错误 |

---

### 3. 刷新Token

#### 基本信息
- **接口路径**: `POST /api/auth/refresh-token`
- **接口描述**: 使用refresh token获取新的access token，并更新Redis存储
- **认证要求**: 需要有效的refresh token

#### 请求参数

**请求头**
```
Content-Type: application/x-www-form-urlencoded
```

**请求参数**
```
refreshToken=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ0b2tlblR5cGUiOiJyZWZyZXNoIn0.signature
```

**参数说明**
| 字段 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| refreshToken | string | 是 | 有效的refresh token | eyJhbGciOiJIUzUxMiJ9... |

#### 响应数据

**成功响应 (200)**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTY5NTU2MTIwMCwiZXhwIjoxNjk1NjQ3NjAwfQ.newSignature"
}
```

**失败响应 (400)**
```json
{
  "code": 400,
  "message": "刷新Token无效或已过期",
  "data": null
}
```

#### 刷新流程

1. 验证refresh token的JWT格式和签名
2. 检查token类型是否为"refresh"
3. 从token中提取用户信息（username, userId）
4. 检查Redis中存储的refresh token是否匹配
5. 生成新的access token
6. 将新的access token存储到Redis中（覆盖旧的）
7. 返回新的access token

#### Redis更新

刷新成功后会更新Redis中的数据：
- **Access Token**: `portal:access_token:{username}:{userId}` → `{newAccessToken}` (重置24小时过期时间)

#### 错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 400 | 刷新Token不能为空 | 请求参数验证失败 |
| 400 | 刷新Token无效或已过期 | refresh token无效、过期或不匹配 |
| 500 | Token刷新失败: {具体错误信息} | 服务器内部错误 |

---

### 4. 强制用户下线

#### 基本信息
- **接口路径**: `POST /api/auth/force-logout`
- **接口描述**: 管理员接口，强制指定token的用户下线，清除Redis并添加到黑名单
- **认证要求**: 需要管理员权限（具体权限控制由上层实现）

#### 请求参数

**请求头**
```
Content-Type: application/x-www-form-urlencoded
```

**请求参数**
```
token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxfQ.signature
```

**参数说明**
| 字段 | 类型 | 必填 | 说明 | 示例 |
|------|------|------|------|------|
| token | string | 是 | 待强制下线的用户access token | eyJhbGciOiJIUzUxMiJ9... |

#### 响应数据

**成功响应 (200)**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "用户下线成功"
}
```

**失败响应 (400/500)**
```json
{
  "code": 400,
  "message": "Token格式无效",
  "data": null
}
```

#### 下线操作流程

1. 从token中提取用户信息（username, userId）
2. 将token添加到黑名单：`portal:token_blacklist:{token}` → `"revoked"` (24小时过期)
3. 删除Redis中的access token：`portal:access_token:{username}:{userId}`
4. 删除Redis中的refresh token：`portal:refresh_token:{username}`
5. 记录操作日志

#### Redis变更

强制下线后会进行以下Redis操作：
- **添加黑名单**: `portal:token_blacklist:{token}` → `"revoked"` (24小时过期)
- **删除Access Token**: `portal:access_token:{username}:{userId}`
- **删除Refresh Token**: `portal:refresh_token:{username}`

#### 错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 400 | Token不能为空 | 请求参数验证失败 |
| 400 | Token格式无效 | 无法从token中解析用户信息 |
| 500 | 用户下线失败: {具体错误信息} | 服务器内部错误 |

---

### 5. 用户注销

#### 基本信息
- **接口路径**: `POST /api/auth/logout`
- **接口描述**: 用户主动注销登录状态，清除Redis存储并添加token到黑名单
- **认证要求**: 需要有效的access token

#### 请求参数

**请求头**
```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyMTIzIiwidXNlcklkIjoxfQ.signature
```

#### 响应数据

**成功响应 (200)**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "注销成功"
}
```

**失败响应 (400/500)**
```json
{
  "code": 400,
  "message": "注销失败",
  "data": null
}
```

#### 注销流程

1. 从请求头中提取token（去除"Bearer "前缀）
2. 如果token为空，直接返回成功（幂等性）
3. 调用tokenService.revokeToken()方法：
   - 将token添加到黑名单
   - 删除Redis中的access token和refresh token
4. 记录注销日志

#### Redis变更

注销成功后会进行以下Redis操作：
- **添加黑名单**: `portal:token_blacklist:{token}` → `"revoked"` (24小时过期)
- **删除Access Token**: `portal:access_token:{username}:{userId}`
- **删除Refresh Token**: `portal:refresh_token:{username}`

#### 错误码

| 错误码 | 错误信息 | 说明 |
|--------|----------|------|
| 500 | 注销失败: {具体错误信息} | 服务器内部错误 |

---

## 通用错误码

### HTTP状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 认证失败 |
| 500 | 服务器内部错误 |

### 业务错误码

| 错误码 | 错误信息模板 | 说明 |
|--------|--------------|------|
| 200 | 操作成功 | 成功响应 |
| 400 | {字段名}不能为空 | 参数验证失败 |
| 400 | {字段名}格式不正确 | 格式验证失败 |
| 401 | 请先登录 | 未提供认证信息 |
| 401 | Token无效 | token验证失败 |
| 401 | Token验证失败 | token验证失败 |
| 500 | {操作}失败: {具体错误信息} | 服务器内部错误 |

## 安全注意事项

### 1. Token传输安全
- 所有接口都应通过HTTPS传输
- Token不应出现在URL参数中
- 建议使用Authorization头传输token

### 2. Token存储安全
- 客户端应安全存储token（避免XSS攻击）
- Refresh token应比access token更安全地存储
- 不建议将token存储在localStorage中

### 3. 接口访问控制
- validate-token接口应限制只允许网关访问
- force-logout接口应加入管理员权限控制
- 建议添加接口访问频率限制

### 4. Redis安全
- Redis连接应使用密码认证
- 生产环境建议使用Redis集群
- 定期监控Redis内存使用情况

## 测试用例

### 登录接口测试
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Token验证接口测试
```bash
curl -X POST http://localhost:8080/api/auth/validate-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'token=eyJhbGciOiJIUzUxMiJ9...'
```

### 刷新Token接口测试
```bash
curl -X POST http://localhost:8080/api/auth/refresh-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'refreshToken=eyJhbGciOiJIUzUxMiJ9...'
```

### 强制下线接口测试
```bash
curl -X POST http://localhost:8080/api/auth/force-logout \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'token=eyJhbGciOiJIUzUxMiJ9...'
```

### 注销接口测试
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

---

**文档版本**: 1.0
**最后更新**: 2025-09-24
**维护人员**: 开发团队