# Kong网关认证接口文档

## 概述

本文档描述了专门为Kong网关提供的认证接口，包括Token验证和强制用户下线功能。

## 接口列表

### 1. 测试登录

#### 基本信息
- **接口路径**: `POST /api/auth/test-login`
- **接口描述**: 测试接口，生成测试用的Token，5分钟过期
- **认证要求**: 无需认证

#### 请求参数

**请求头**
```
Content-Type: application/json
```

**请求参数**
无需参数

#### 响应数据

**成功响应 (200)**
```json
{
    "code": 200,
    "message": "测试Token生成成功",
    "data": {
        "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTM1ODgwNzAiLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImV4cCI6MTc1ODg2NzEwOSwidXNlcklkIjozMywiaWF0IjoxNzU4NzgwNzA5MTUyfQ.UPiORz7OG5Ot0eWpNedURPhuq0Xe6GgvbYC-J33DpoOqOnEhuTc8Uh9UI8i-N7VzxZFfT5X1e0MKQnVGwlQabg",
        "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTM1ODgwNzAiLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6InJlZnJlc2giLCJleHAiOjE3NTkzODU1MDksInVzZXJJZCI6MzMsImlhdCI6MTc1ODc4MDcwOTE1M30.G9cYhrScWhACY6I1UIEAbU3GT8YPyGb-Ooou4iyd6AjAFMElJrKjGfCp3AWBaZzyQEa-M_PD2pBCwr9_uc5H4A",
        "tokenType": "Bearer",
        "expiresIn": 300,
        "userInfo": {
            "id": 33,
            "username": "553588070",
            "email": "test@example.com",
            "nickname": "测试用户"
        }
    }
}
```

**失败响应 (500)**
```json
{
  "code": 500,
  "message": "生成测试Token失败: {具体错误信息}",
  "data": null
}
```

#### 接口特点

1. 使用固定的测试用户信息（用户名：553588070，用户ID：33）
2. Token过期时间设置为5分钟（300秒）
3. 同时生成access token和refresh token
4. 将Token存储到Redis中，便于后续验证
5. 无需任何参数，快速生成测试Token

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

### 3. 强制用户下线

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

### 1. 接口访问控制
- validate-token接口应限制只允许网关访问
- force-logout接口应加入管理员权限控制
- 建议添加接口访问频率限制

### 2. Token传输安全
- 所有接口都应通过HTTPS传输
- Token不应出现在URL参数中
- 网关内部调用可以使用form参数传输

### 3. Redis安全
- Redis连接应使用密码认证
- 生产环境建议使用Redis集群
- 定期监控Redis内存使用情况

## 测试用例

### 测试登录接口

#### Bash调用
```bash
# 生成测试Token
curl -X POST http://localhost:18085/api/auth/test-login \
  -H "Content-Type: application/json"

# 提取token并保存到变量（使用jq工具）
TOKEN=$(curl -s -X POST http://localhost:18085/api/auth/test-login \
  -H "Content-Type: application/json" | jq -r '.data.accessToken')

echo "Generated Token: $TOKEN"
```

#### Windows CMD调用
```cmd
REM 生成测试Token
curl -X POST http://localhost:18085/api/auth/test-login -H "Content-Type: application/json"
```

---

### Token验证接口测试

#### Bash调用
```bash
# 使用固定token测试
curl -X POST http://localhost:18085/api/auth/validate-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTM1ODgwNzAiLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImV4cCI6MTc1ODg2NDgwOSwidXNlcklkIjozMywiaWF0IjoxNzU4Nzc4NDA5ODcwfQ.meOqmA7uoObp7-EbC98I-m9Ax6Zffy315nBnAjj5fXqhGigV8yXozECFX4IzHvynfU-TvRTUspL8eNgnFfWDIg'

# 使用动态生成的token测试
TOKEN=$(curl -s -X POST http://localhost:18085/api/auth/test-login \
  -H "Content-Type: application/json" | jq -r '.data.accessToken')

curl -X POST http://localhost:18085/api/auth/validate-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$TOKEN"
```

#### Windows CMD调用
```cmd
REM 使用固定token测试
curl -X POST http://localhost:18085/api/auth/validate-token -H "Content-Type: application/x-www-form-urlencoded" -d "token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTM1ODgwNzAiLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImV4cCI6MTc1ODg2NDgwOSwidXNlcklkIjozMywiaWF0IjoxNzU4Nzc4NDA5ODcwfQ.meOqmA7uoObp7-EbC98I-m9Ax6Zffy315nBnAjj5fXqhGigV8yXozECFX4IzHvynfU-TvRTUspL8eNgnFfWDIg"
```

---

### 强制下线接口测试

#### Bash调用
```bash
# 使用固定token测试
curl -X POST http://localhost:18085/api/auth/force-logout \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTM1ODgwNzAiLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImV4cCI6MTc1ODg2NDgwOSwidXNlcklkIjozMywiaWF0IjoxNzU4Nzc4NDA5ODcwfQ.meOqmA7uoObp7-EbC98I-m9Ax6Zffy315nBnAjj5fXqhGigV8yXozECFX4IzHvynfU-TvRTUspL8eNgnFfWDIg'

# 使用动态生成的token测试
TOKEN=$(curl -s -X POST http://localhost:18085/api/auth/test-login \
  -H "Content-Type: application/json" | jq -r '.data.accessToken')

curl -X POST http://localhost:18085/api/auth/force-logout \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$TOKEN"
```

#### Windows CMD调用
```cmd
REM 使用固定token测试
curl -X POST http://localhost:18085/api/auth/force-logout -H "Content-Type: application/x-www-form-urlencoded" -d "token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1NTM1ODgwNzAiLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImV4cCI6MTc1ODg2NDgwOSwidXNlcklkIjozMywiaWF0IjoxNzU4Nzc4NDA5ODcwfQ.meOqmA7uoObp7-EbC98I-m9Ax6Zffy315nBnAjj5fXqhGigV8yXozECFX4IzHvynfU-TvRTUspL8eNgnFfWDIg"
```

---

### 完整测试流程示例

#### Bash完整测试流程
```bash
#!/bin/bash

echo "=== Kong网关认证接口测试 ==="

# 1. 生成测试Token
echo "1. 生成测试Token..."
TOKEN=$(curl -s -X POST http://localhost:18085/api/auth/test-login \
  -H "Content-Type: application/json" | jq -r '.data.accessToken')

if [ "$TOKEN" != "null" ] && [ -n "$TOKEN" ]; then
  echo "✅ Token生成成功: $TOKEN"
else
  echo "❌ Token生成失败"
  exit 1
fi

# 2. 验证Token
echo "2. 验证Token..."
VALIDATE_RESULT=$(curl -s -X POST http://localhost:18085/api/auth/validate-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$TOKEN")

echo "验证结果: $VALIDATE_RESULT"

# 3. 强制下线
echo "3. 强制下线..."
LOGOUT_RESULT=$(curl -s -X POST http://localhost:18085/api/auth/force-logout \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$TOKEN")

echo "下线结果: $LOGOUT_RESULT"

# 4. 再次验证Token（应该失败）
echo "4. 验证已下线的Token..."
VALIDATE_AGAIN=$(curl -s -X POST http://localhost:18085/api/auth/validate-token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=$TOKEN")

echo "再次验证结果: $VALIDATE_AGAIN"

echo "=== 测试完成 ==="
```

---

**文档版本**: 1.0
**最后更新**: 2025-09-25
**维护人员**: 开发团队
**适用范围**: Kong网关集成