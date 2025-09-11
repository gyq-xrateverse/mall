# 📧 邮箱验证码发送测试指南

## 🎯 测试目标
向 `gaoyongqiang@xrateverse.com` 邮箱发送验证码邮件，验证邮件服务功能是否正常。

## 🔧 邮件服务配置

基于 mailx 项目的生产级配置：

```bash
MAIL_HOST=smtp.exmail.qq.com
MAIL_PORT=465
MAIL_USERNAME=vcode@xrateverse.com  
MAIL_PASSWORD=NNyqSi67bLuGLzpv
MAIL_FROM_NAME=BEILV AI
MAIL_SSL_ENABLE=true
MAIL_PROTOCOL=smtps
```

## 🧪 测试方式

### 方式1: 使用API测试（推荐）

如果应用已启动，直接测试API接口：

```bash
./test-email-with-curl.sh
```

这将测试：
- ✅ 发送注册验证码API
- ✅ 发送密码重置验证码API  
- ✅ 邮箱存在性检查API

### 方式2: 启动应用后测试

1. **设置环境变量：**
```bash
export MAIL_HOST=smtp.exmail.qq.com
export MAIL_PORT=465
export MAIL_USERNAME=vcode@xrateverse.com
export MAIL_PASSWORD=NNyqSi67bLuGLzpv
export MAIL_SSL_ENABLE=true
export MAIL_PROTOCOL=smtps
```

2. **启动应用：**
```bash
mvn spring-boot:run -Dspring.profiles.active=dev
```

3. **测试API接口：**
```bash
# 发送注册验证码 (codeType: 1)
curl -X POST http://localhost:8085/api/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"email":"gaoyongqiang@xrateverse.com","codeType":1}'

# 发送密码重置验证码 (codeType: 3)
curl -X POST http://localhost:8085/api/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"email":"gaoyongqiang@xrateverse.com","codeType":3}'
```

### 方式3: 单元测试方式

如果Java环境可用：

```bash
# 运行所有邮件相关测试
./run-tests.sh

# 或运行真实邮件发送测试
./send-test-email.sh
```

## 📧 预期结果

测试成功后，邮箱 `gaoyongqiang@xrateverse.com` 应该收到：

1. **注册验证码邮件**
   - 主题：BEILV AI - 邮箱验证码
   - 内容：包含6位数字验证码
   - 发件人：BEILV AI <vcode@xrateverse.com>

2. **密码重置验证码邮件**
   - 主题：BEILV AI - 密码重置验证码
   - 内容：包含6位数字验证码
   - 发件人：BEILV AI <vcode@xrateverse.com>

3. **HTML格式测试邮件**（如果运行完整测试）
   - 精美的HTML格式
   - 包含测试信息和验证码

## ⚠️ 注意事项

1. **检查垃圾邮件文件夹** - 第一次发送可能被标记为垃圾邮件
2. **验证码有效期** - 验证码5分钟内有效
3. **发送限制** - 每分钟最多发送1次，每日最多10次
4. **网络要求** - 需要能访问 smtp.exmail.qq.com:465

## 🔍 故障排除

### 常见问题：

**❌ 邮件发送失败**
- 检查网络连接
- 确认邮件服务器配置正确
- 查看应用日志获取详细错误

**❌ API调用失败**
- 确认应用已启动在8085端口
- 检查API路径是否正确
- 确认请求格式是否正确

**❌ 未收到邮件**
- 检查垃圾邮件文件夹
- 确认目标邮箱地址正确
- 查看应用日志确认发送状态

### 查看日志：

```bash
# 查看应用日志
tail -f logs/mall-portal.log

# 或启动时直接查看
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 📊 测试报告

测试完成后请确认：
- [ ] API调用返回200状态码
- [ ] 应用日志显示邮件发送成功
- [ ] 目标邮箱收到验证码邮件
- [ ] 验证码格式正确（6位数字）
- [ ] 邮件内容和样式正确

## 🎉 成功标志

当看到以下输出时表示测试成功：

```
✅ 邮件API测试全部通过！
📮 请检查邮箱 gaoyongqiang@xrateverse.com
📁 注意查看垃圾邮件文件夹
🕐 验证码有效期为5分钟
```

这表明邮箱验证登录功能已经完整配置并可以正常使用！