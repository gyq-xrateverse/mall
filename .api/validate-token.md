# 网关Token验证接口

## 接口概述

**接口名称：** 网关Token验证接口
**接口路径：** `/api/auth/validate-token`
**请求方式：** `POST`
**接口用途：** 供网关调用，验证用户Token的有效性并返回用户信息

## 请求参数

### 请求URL
```
POST /api/auth/validate-token
```

### 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| token | String | 是 | 待验证的JWT Token |

### 请求示例
```bash
curl -X POST "http://localhost:8085/api/auth/validate-token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VyVHlwZSI6Im1lbWJlciIsInRva2VuVHlwZSI6ImFjY2VzcyIsImlhdCI6MTY5NTQ2NDQwMCwiZXhwIjoxNjk1NTUwODAwfQ.signature"
```

## 响应结果

### 成功响应 (Code: 200)

**响应格式：**
```json
{
  "code": 200,
  "message": "Token验证成功",
  "data": {
    "username": "test@example.com",
    "userId": 1,
    "userType": "member",
    "valid": true
  }
}
```

**响应字段说明：**
| 字段名 | 类型 | 说明 |
|--------|------|------|
| code | Long | 响应状态码，200表示成功 |
| message | String | 响应消息 |
| data | Object | 用户信息对象 |
| data.username | String | 用户名（通常是邮箱） |
| data.userId | Long | 用户ID |
| data.userType | String | 用户类型，固定为"member" |
| data.valid | Boolean | Token有效性标识，固定为true |

### 失败响应 (Code: 500)

**响应格式：**
```json
{
  "code": 500,
  "message": "Token验证失败",
  "data": null
}
```

**失败情况：**
- Token格式不正确
- Token已过期
- Token签名验证失败
- Token不是访问类型（access token）
- Token用户类型不是member
- 其他Token解析异常

## 验证逻辑

### Token验证流程
1. **格式验证：** 检查Token是否为有效的JWT格式
2. **签名验证：** 使用密钥验证Token签名
3. **过期检查：** 检查Token是否已过期
4. **类型验证：** 确认Token类型为"access"
5. **用户类型验证：** 确认用户类型为"member"
6. **信息提取：** 从Token中提取用户信息

### Token结构说明
验证的JWT Token包含以下Claims：
- `sub`: 用户名（通常是邮箱）
- `userId`: 用户ID
- `userType`: 用户类型（必须为"member"）
- `tokenType`: Token类型（必须为"access"）
- `iat`: 签发时间
- `exp`: 过期时间
- `registerType`: 注册类型（可选）

## 使用场景

### 网关认证流程
1. 客户端请求携带Token访问受保护的API
2. 网关拦截请求，提取Token
3. 网关调用此接口验证Token
4. 根据验证结果决定是否放行请求

### 调用时机
- 用户访问需要认证的API接口时
- Token缓存失效需要重新验证时
- 定期检查Token有效性时

## 安全说明

### 安全特性
- 使用HS512算法进行Token签名
- 验证Token的完整性和真实性
- 检查Token过期时间
- 记录验证失败的安全日志

### 注意事项
- Token验证失败会记录警告日志
- Token验证异常会记录错误日志
- 此接口仅供网关内部调用，不对外开放
- 建议网关对此接口的调用进行频率限制

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | Token验证成功 | 继续处理业务逻辑 |
| 500 | Token验证失败 | 返回认证失败，要求重新登录 |

## 开发调试

### 测试用例
```bash
# 测试有效Token
curl -X POST "http://localhost:8085/api/auth/validate-token" \
  -d "token=VALID_JWT_TOKEN_HERE"

# 测试无效Token
curl -X POST "http://localhost:8085/api/auth/validate-token" \
  -d "token=invalid_token"

# 测试空Token
curl -X POST "http://localhost:8085/api/auth/validate-token" \
  -d "token="
```

### 日志查看
```bash
# 查看验证成功日志
grep "网关Token验证成功" application.log

# 查看验证失败日志
grep "网关Token验证失败" application.log

# 查看验证异常日志
grep "网关Token验证异常" application.log
```

## 更新历史

| 版本 | 日期 | 更新内容 |
|------|------|----------|
| 1.0.0 | 2025-09-23 | 初始版本，支持基础Token验证功能 |