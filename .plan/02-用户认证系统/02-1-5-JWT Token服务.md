# 02-1-5: JWT Token服务

## 任务概述
**时间估算**: 1.5小时  
**优先级**: 最高  
**依赖关系**: 02-1-4 验证码服务完成  
**负责模块**: mall-portal后端

## 详细任务清单

### 1. 添加JWT依赖
- [ ] 在pom.xml中添加JJWT依赖
- [ ] 检查JWT版本与Spring Boot兼容性
- [ ] 同步依赖到开发环境

### 2. 配置JWT参数
- [ ] 在application.yml中配置JWT密钥
- [ ] 设置访问令牌过期时间（24小时）
- [ ] 设置刷新令牌过期时间（7天）
- [ ] 配置JWT签发者信息
- [ ] 使用环境变量管理敏感配置

### 3. 实现JWT工具类
- [ ] 创建JwtUtils工具类
- [ ] 注入配置参数
- [ ] 实现访问令牌生成方法
- [ ] 实现刷新令牌生成方法
- [ ] 实现令牌解析方法
- [ ] 实现令牌验证方法
- [ ] 添加令牌信息提取方法

### 4. 实现令牌生成逻辑
- [ ] generateToken方法生成访问令牌
- [ ] 在Claims中包含用户核心信息（ID、邮箱、用户名）
- [ ] generateRefreshToken方法生成刷新令牌
- [ ] 设置合适的令牌过期时间
- [ ] 使用HS512算法签名

### 5. 实现令牌解析和验证
- [ ] getClaimsFromToken方法解析令牌
- [ ] 处理令牌格式异常和签名验证失败
- [ ] getEmailFromToken提取用户邮箱
- [ ] getMemberIdFromToken提取用户ID
- [ ] isTokenExpired检查令牌是否过期
- [ ] validateToken综合验证令牌有效性

### 6. 异常处理和日志
- [ ] 处理JWT解析异常
- [ ] 记录令牌验证失败日志
- [ ] 添加安全相关的审计日志

## 验收标准
- [x] JWT令牌生成和解析功能正确
- [x] 支持访问令牌和刷新令牌两种类型
- [x] 令牌验证逻辑完整可靠
- [x] 安全密钥配置正确
- [x] 异常处理完善
- [x] 令牌过期机制正常工作

## 交付物
1. **JWT工具类**（JwtUtils.java）
2. **JWT配置**（application.yml JWT部分）
3. **JWT依赖**（pom.xml更新）
4. **单元测试类**（JwtUtilsTest.java）
5. **JWT使用文档**

## 技术要点

### JWT依赖
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### JWT配置
```yaml
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-change-in-production}
  expiration: 86400 # 24小时（秒）
  refresh-expiration: 604800 # 7天（秒）
  issuer: beilv-ai
```

### 核心工具方法
```java
@Component
public class JwtUtils {
    
    // 生成访问令牌
    public String generateToken(UmsMember member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getId());
        claims.put("email", member.getEmail());
        claims.put("username", member.getUsername());
        return createToken(claims, member.getEmail());
    }
    
    // 生成刷新令牌
    public String generateRefreshToken(UmsMember member) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getId());
        claims.put("tokenType", "refresh");
        // 设置更长的过期时间
    }
    
    // 验证令牌
    public boolean validateToken(String token, UmsMember member) {
        String email = getEmailFromToken(token);
        return email != null && email.equals(member.getEmail()) && !isTokenExpired(token);
    }
}
```

### 令牌结构设计
**访问令牌Claims**:
- `memberId`: 用户ID
- `email`: 用户邮箱
- `username`: 用户名
- `sub`: 主题（邮箱）
- `iss`: 签发者
- `iat`: 签发时间
- `exp`: 过期时间

**刷新令牌Claims**:
- `memberId`: 用户ID
- `tokenType`: "refresh"
- `sub`: 主题（邮箱）
- `iss`: 签发者
- `iat`: 签发时间
- `exp`: 过期时间（更长）

### 安全考虑
- 使用256位强密钥
- 密钥通过环境变量配置
- 使用HS512签名算法
- 合理设置令牌过期时间
- 记录令牌验证失败日志

### 令牌使用流程
1. **登录时**: 生成访问令牌和刷新令牌返回给客户端
2. **API访问**: 客户端在请求头中携带访问令牌
3. **令牌刷新**: 访问令牌过期时使用刷新令牌获取新的访问令牌
4. **登出**: 客户端删除本地令牌（可选实现令牌黑名单）

## 环境变量
需要设置以下环境变量：
- `JWT_SECRET`: JWT签名密钥（至少256位）

## 单元测试用例
- 令牌生成测试
- 令牌解析测试
- 令牌验证测试
- 过期令牌测试
- 无效令牌测试
- 篡改令牌测试

## 下一步
完成后进入 `02-2-1-邮箱验证码认证控制器`