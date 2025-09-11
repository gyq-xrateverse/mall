# QQ邮箱SMTP配置指南

## 🔑 获取QQ邮箱授权码

### 步骤1：登录QQ邮箱
访问 https://mail.qq.com 并登录你的QQ邮箱

### 步骤2：进入设置
点击邮箱界面右上角的"设置" → 选择"账户"

### 步骤3：开启SMTP服务
找到 **"POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务"** 部分

开启以下服务：
- ✅ **IMAP/SMTP服务** 
- ✅ **POP3/SMTP服务**

### 步骤4：获取授权码
1. 点击"生成授权码"
2. 验证手机号码（发送短信验证）
3. 系统会生成一个16位的授权码，如：`abcdgefhijklmnop`
4. **重要**：这个授权码就是你的邮箱密码，请妥善保存

### 步骤5：更新配置
将获取到的授权码更新到配置文件中：

**在 `.env` 文件中**：
```env
MAIL_PASSWORD=你的16位授权码
```

**在 `application-dev.yml` 中**：
```yaml
password: ${MAIL_PASSWORD:你的16位授权码}
```

## 📧 测试邮件发送

配置完成后重启后端服务，然后测试：

```bash
curl -X POST http://localhost:8085/api/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"email":"你的邮箱地址@qq.com","codeType":2}'
```

## ⚠️ 注意事项

1. **授权码不是QQ密码**：授权码是专门用于第三方客户端的16位随机字符串
2. **安全性**：授权码具有邮箱完整权限，请勿泄露
3. **有效期**：授权码长期有效，除非手动删除
4. **多个授权码**：可以生成多个授权码，用于不同应用

## 🔧 常见问题

**Q: 提示"授权码错误"？**
A: 
1. 确认是否使用授权码而不是QQ密码
2. 检查授权码是否完整（16位）
3. 确认SMTP服务已开启

**Q: 连接超时？**
A:
1. 检查网络连接
2. 确认端口587未被防火墙阻止
3. 尝试使用465端口（SSL）

## 🎯 配置模板

完整的QQ邮箱配置：

```yaml
spring:
  mail:
    host: smtp.qq.com
    port: 587
    username: 你的QQ号@qq.com
    password: 你的授权码
    protocol: smtp
    default-encoding: UTF-8
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
          ssl:
            trust: "*"
```

---

配置完成后就可以真实发送验证码邮件了！🎉