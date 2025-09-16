# 文件存储服务测试指南

## 概述

本测试套件提供了对文件存储服务的完整测试覆盖，包括单元测试、集成测试和API测试。

## 测试结构

```
src/test/java/com/macro/mall/portal/
├── FileStorageTestSuite.java                    # 测试套件入口
├── service/
│   ├── FileStorageServiceTest.java              # 存储服务接口测试
│   ├── MinioFileStorageServiceTest.java         # MinIO实现测试
│   ├── AliyunOssFileStorageServiceTest.java     # 阿里云OSS实现测试
│   └── PortalCaseServiceIntegrationTest.java    # 案例管理集成测试
└── controller/
    └── FileUploadControllerTest.java            # 文件上传API测试
```

## 测试内容

### 1. FileStorageService 接口测试
- ✅ 服务注入验证
- ✅ 文本/图片/视频文件上传
- ✅ 自定义ObjectName上传
- ✅ URL构建功能
- ✅ CDN URL构建
- ✅ ObjectName提取
- ✅ 文件删除
- ✅ 异常处理
- ✅ 特殊文件名处理

### 2. MinIO存储服务测试
- ✅ MinIO配置验证
- ✅ MinIO URL格式验证
- ✅ 文件上传和桶管理
- ✅ 批量文件操作
- ✅ 路径处理
- ✅ 文件删除
- ✅ ObjectName提取
- ✅ 错误处理
- ✅ 大文件上传测试
- ✅ 性能基准测试

### 3. 阿里云OSS存储服务测试
- ✅ OSS配置验证
- ✅ OSS URL格式验证
- ✅ CDN URL构建
- ✅ 文件上传
- ✅ 内网endpoint优化
- ✅ ObjectName提取
- ✅ 多媒体文件上传
- ✅ 文件删除
- ✅ 路径和中文支持
- ✅ 性能和稳定性
- ✅ 错误处理
- ✅ MinIO兼容性

### 4. 案例管理集成测试
- ✅ 测试数据创建
- ✅ 文件上传
- ✅ 案例详情URL构建
- ✅ 案例列表URL构建
- ✅ 分类列表
- ✅ 点赞功能
- ✅ 浏览功能
- ✅ 热门/最新案例
- ✅ 懒加载列表
- ✅ 搜索功能
- ✅ 存储切换兼容性
- ✅ 数据完整性
- ✅ 测试数据清理

### 5. 文件上传API测试
- ✅ 图片/视频上传成功
- ✅ 不支持格式验证
- ✅ 空文件名验证
- ✅ 文件大小限制
- ✅ 各种图片/视频格式
- ✅ 中文文件名
- ✅ 文件删除API
- ✅ 并发上传
- ✅ ObjectName格式
- ✅ URL和ObjectName返回
- ✅ 存储服务兼容性

## 运行测试

### 前置条件

1. **数据库准备**：测试使用H2内存数据库，无需额外配置

2. **MinIO服务**（如果测试MinIO实现）：
   ```bash
   # 使用Docker启动MinIO
   docker run -p 9000:9000 -p 9001:9001 \
     -e "MINIO_ACCESS_KEY=minioadmin" \
     -e "MINIO_SECRET_KEY=minioadmin" \
     minio/minio server /data --console-address ":9001"
   ```

3. **阿里云OSS配置**（如果测试OSS实现）：
   - 在 `application-test.yml` 中配置真实的OSS参数
   - 或者使用Mock测试

### 运行方式

#### 1. 运行完整测试套件
```bash
mvn test -Dtest=FileStorageTestSuite
```

#### 2. 运行单个测试类
```bash
# 接口测试
mvn test -Dtest=FileStorageServiceTest

# MinIO测试
mvn test -Dtest=MinioFileStorageServiceTest

# OSS测试
mvn test -Dtest=AliyunOssFileStorageServiceTest

# 集成测试
mvn test -Dtest=PortalCaseServiceIntegrationTest

# API测试
mvn test -Dtest=FileUploadControllerTest
```

#### 3. 切换存储类型测试
```bash
# 测试MinIO实现
mvn test -Dtest=FileStorageServiceTest -Dstorage.type=minio

# 测试OSS实现
mvn test -Dtest=FileStorageServiceTest -Dstorage.type=aliyun-oss
```

## 测试配置

### application-test.yml 关键配置

```yaml
# 存储服务测试配置
storage:
  type: minio # 或 aliyun-oss

# MinIO测试配置
minio:
  endpoint: http://localhost:9000
  bucketName: test-mall
  accessKey: minioadmin
  secretKey: minioadmin

# 阿里云OSS测试配置
aliyun:
  oss:
    endpoint: https://oss-cn-hangzhou.aliyuncs.com
    bucketName: test-mall-oss
    accessKeyId: test-access-key-id
    accessKeySecret: test-access-key-secret
    cdnDomain: https://test-cdn.example.com

# 测试配置
test:
  mock-storage-service: true  # 是否使用Mock存储服务
  test-file-cleanup: true     # 测试后是否清理文件
```

## 测试最佳实践

### 1. 文件清理
- 每个测试方法后自动清理上传的文件
- 避免测试文件积累影响存储空间

### 2. 异常处理
- 测试各种异常情况
- 验证错误消息和状态码

### 3. 兼容性测试
- 确保MinIO和OSS实现的行为一致
- 验证ObjectName存储格式兼容

### 4. 性能测试
- 基准测试上传性能
- 并发上传测试

## 故障排除

### 常见问题

1. **MinIO连接失败**
   - 检查MinIO服务是否启动
   - 验证端口和凭据配置

2. **OSS配置错误**
   - 检查OSS账号权限
   - 验证bucket存在且可访问

3. **测试数据库问题**
   - H2数据库会自动重置
   - 检查数据库连接配置

4. **文件清理失败**
   - 网络问题可能导致清理失败
   - 手动清理测试bucket中的文件

### 调试技巧

1. **查看详细日志**
   ```bash
   mvn test -Dtest=FileStorageServiceTest -Dlogging.level.com.macro.mall=DEBUG
   ```

2. **跳过文件清理**（调试时）
   ```yaml
   test:
     test-file-cleanup: false
   ```

3. **使用Mock服务**（无需真实存储）
   ```yaml
   test:
     mock-storage-service: true
   ```

## 测试报告

测试运行后会生成详细的报告，包括：
- ✅ 通过的测试数量
- ❌ 失败的测试详情
- ⏱️ 性能指标
- 📊 覆盖率统计

## 扩展测试

要添加新的存储服务实现测试：

1. 创建新的测试类：`NewStorageServiceTest.java`
2. 继承现有测试模式
3. 添加到 `FileStorageTestSuite` 中
4. 更新配置文件支持新的存储类型

## 持续集成

这些测试可以集成到CI/CD流水线中：

```yaml
# GitHub Actions 示例
- name: Run Storage Tests
  run: mvn test -Dtest=FileStorageTestSuite
  env:
    STORAGE_TYPE: minio
    MINIO_ENDPOINT: http://localhost:9000
```