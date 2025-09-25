# 认证系统测试执行指南

## 概述

本文档描述了如何执行认证系统的各类测试，包括单元测试、集成测试和性能测试。

## 测试文件结构

```
mall-portal/src/test/java/
├── com/macro/mall/portal/controller/
│   ├── AuthControllerTest.java          # 原有的认证控制器测试
│   └── AuthApiTest.java                 # 新增：基于API文档的完整接口测试
├── com/macro/mall/portal/service/
│   └── TokenServiceRedisTest.java       # 新增：TokenService Redis功能测试
└── com/macro/mall/portal/integration/
    └── AuthenticationIntegrationTest.java # 新增：认证系统集成测试

mall-security/src/test/java/
└── com/macro/mall/security/util/
    └── PortalJwtTokenUtilRedisTest.java  # 新增：JWT工具类Redis功能测试
```

## 测试分类

### 1. API接口测试 (AuthApiTest.java)
测试所有5个核心认证接口：
- ✅ 用户登录 (`POST /api/auth/login`)
- ✅ Token验证 (`POST /api/auth/validate-token`)
- ✅ 刷新Token (`POST /api/auth/refresh-token`)
- ✅ 强制用户下线 (`POST /api/auth/force-logout`)
- ✅ 用户注销 (`POST /api/auth/logout`)

**执行命令：**
```bash
# 执行单个测试类
./mvnw test -Dtest=AuthApiTest

# 执行特定测试组
./mvnw test -Dtest=AuthApiTest#LoginApiTest
./mvnw test -Dtest=AuthApiTest#ValidateTokenApiTest
./mvnw test -Dtest=AuthApiTest#RefreshTokenApiTest
./mvnw test -Dtest=AuthApiTest#ForceLogoutApiTest
./mvnw test -Dtest=AuthApiTest#LogoutApiTest
```

### 2. Redis功能测试 (TokenServiceRedisTest.java)
专注测试Token服务的Redis存储功能：
- ✅ Access Token Redis存储
- ✅ Refresh Token Redis存储
- ✅ Token注销和黑名单
- ✅ Redis异常处理
- ✅ 性能和并发测试

**执行命令：**
```bash
# 执行Redis功能测试
./mvnw test -Dtest=TokenServiceRedisTest

# 执行特定测试组
./mvnw test -Dtest=TokenServiceRedisTest#AccessTokenRedisTest
./mvnw test -Dtest=TokenServiceRedisTest#RefreshTokenRedisTest
./mvnw test -Dtest=TokenServiceRedisTest#TokenRevocationTest
```

### 3. JWT工具类Redis测试 (PortalJwtTokenUtilRedisTest.java)
测试JWT工具类的Redis集成：
- ✅ validateAccessToken Redis检查
- ✅ Token信息提取
- ✅ Token类型验证
- ✅ 并发和性能测试

**执行命令：**
```bash
# 执行JWT工具类测试
./mvnw test -Dtest=PortalJwtTokenUtilRedisTest

# 在security模块中执行
cd mall-security
../mvnw test -Dtest=PortalJwtTokenUtilRedisTest
```

### 4. 集成测试 (AuthenticationIntegrationTest.java)
测试完整的认证流程：
- ✅ 完整认证流程（登录→验证→注销）
- ✅ Token刷新流程
- ✅ 强制下线流程
- ✅ Redis存储验证
- ✅ 并发场景测试

**执行命令：**
```bash
# 需要真实的Redis和数据库环境
./mvnw test -Dtest=AuthenticationIntegrationTest -Dspring.profiles.active=test
```

## 测试环境配置

### 1. 测试配置文件

创建 `application-test.yml`：
```yaml
# Redis测试配置
spring:
  redis:
    host: localhost
    port: 6379
    database: 15  # 使用独立的测试数据库
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms

# JWT测试配置
jwt:
  secret: test-jwt-secret-key-for-testing-purposes-only-do-not-use-in-production
  expiration: 86400
  refresh-expiration: 604800
  tokenHead: "Bearer "

# 数据库测试配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall_test?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}

# 日志配置
logging:
  level:
    com.macro.mall: DEBUG
    org.springframework.data.redis: DEBUG
```

### 2. Docker测试环境

使用Docker启动测试依赖：
```bash
# 启动Redis (用于测试)
docker run -d --name redis-test -p 6379:6379 redis:7-alpine

# 启动MySQL (用于集成测试)
docker run -d --name mysql-test \
  -e MYSQL_ROOT_PASSWORD=test123 \
  -e MYSQL_DATABASE=mall_test \
  -p 3306:3306 mysql:8.0
```

### 3. 测试数据准备

集成测试需要的测试数据：
```sql
-- 插入测试用户
INSERT INTO ums_member (username, email, password, status, create_time)
VALUES ('integrationUser', 'integration@test.com', '$2a$10$encrypted_password', 1, NOW());
```

## 执行所有测试

### 1. 单元测试
```bash
# 执行所有认证相关的单元测试
./mvnw test -Dtest="*Auth*Test" -DfailIfNoTests=false

# 执行特定模块的测试
./mvnw test -pl mall-portal -Dtest="*Auth*"
./mvnw test -pl mall-security -Dtest="*Portal*"
```

### 2. 集成测试
```bash
# 需要先启动Redis和数据库
docker-compose up -d redis mysql

# 执行集成测试
./mvnw test -Dtest="*Integration*Test" -Dspring.profiles.active=test
```

### 3. 完整测试套件
```bash
# 执行所有认证相关测试
./mvnw test -Dtest="*Auth*,*Token*,*Portal*" -Dspring.profiles.active=test
```

## 测试报告

### 1. 生成测试报告
```bash
# 生成Surefire测试报告
./mvnw surefire-report:report

# 查看报告
open target/site/surefire-report.html
```

### 2. 代码覆盖率
```bash
# 使用JaCoCo生成覆盖率报告
./mvnw test jacoco:report

# 查看覆盖率报告
open target/site/jacoco/index.html
```

## 性能测试

### 1. Token验证性能
```bash
# 执行性能测试
./mvnw test -Dtest="*Performance*" -Dspring.profiles.active=test

# 自定义性能参数
./mvnw test -Dtest=TokenServiceRedisTest#testTokenGenerationPerformance \
  -Dtest.token.count=10000 \
  -Dtest.thread.count=50
```

### 2. 并发测试
```bash
# 执行并发测试
./mvnw test -Dtest="*Concurrent*" -Dspring.profiles.active=test
```

## 常见问题排查

### 1. Redis连接失败
```bash
# 检查Redis服务状态
docker ps | grep redis

# 测试Redis连接
redis-cli ping

# 检查测试配置
cat src/test/resources/application-test.yml
```

### 2. 数据库连接问题
```bash
# 检查MySQL服务
docker ps | grep mysql

# 测试数据库连接
mysql -h localhost -P 3306 -u root -p mall_test
```

### 3. 测试失败分析
```bash
# 查看详细测试日志
./mvnw test -Dtest=AuthApiTest -X

# 查看特定测试的输出
./mvnw test -Dtest=AuthApiTest#testLoginSuccess -Dtest.verbose=true
```

## 持续集成

### 1. GitHub Actions示例
```yaml
name: Authentication Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    services:
      redis:
        image: redis:7-alpine
        ports:
          - 6379:6379
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: test123
          MYSQL_DATABASE: mall_test
        ports:
          - 3306:3306

    steps:
    - uses: actions/checkout@v3
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'

    - name: Run Authentication Tests
      run: ./mvnw test -Dtest="*Auth*,*Token*,*Portal*" -Dspring.profiles.active=test

    - name: Generate Test Report
      run: ./mvnw surefire-report:report

    - name: Upload Test Results
      uses: actions/upload-artifact@v3
      with:
        name: test-results
        path: target/site/
```

### 2. Jenkins Pipeline示例
```groovy
pipeline {
    agent any
    stages {
        stage('Test') {
            parallel {
                stage('Unit Tests') {
                    steps {
                        sh './mvnw test -Dtest="*Auth*Test" -DfailIfNoTests=false'
                    }
                }
                stage('Integration Tests') {
                    steps {
                        sh './mvnw test -Dtest="*Integration*Test" -Dspring.profiles.active=test'
                    }
                }
            }
        }
        stage('Report') {
            steps {
                publishTestResults testResultsPattern: 'target/surefire-reports/*.xml'
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site',
                    reportFiles: 'surefire-report.html',
                    reportName: 'Test Report'
                ])
            }
        }
    }
}
```

## 测试最佳实践

### 1. 测试命名规范
- 测试类：`{ClassName}Test` 或 `{ClassName}IntegrationTest`
- 测试方法：`test{Scenario}{ExpectedResult}` 或 `should{ExpectedResult}When{Scenario}`
- 显示名称：使用中文描述具体测试场景

### 2. 测试数据管理
- 使用常量定义测试数据
- 每个测试独立准备数据
- 测试后清理资源

### 3. 断言策略
- 优先使用具体的断言方法
- 为断言提供有意义的错误消息
- 验证关键的业务逻辑

### 4. Mock使用
- 只Mock外部依赖
- 验证重要的交互
- 避免过度Mock

---

**文档版本**: 1.0
**最后更新**: 2025-09-24
**维护人员**: 开发团队