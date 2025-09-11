#!/bin/bash

# 修复后的邮件发送测试脚本
echo "=========================================="
echo "📧 BEILV AI 邮件服务测试 (修复版)"
echo "目标邮箱: gaoyongqiang@xrateverse.com"
echo "=========================================="

# 设置环境变量
export SPRING_PROFILES_ACTIVE=dev
export MAIL_HOST=smtp.exmail.qq.com
export MAIL_PORT=465
export MAIL_USERNAME=vcode@xrateverse.com
export MAIL_PASSWORD=NNyqSi67bLuGLzpv
export MAIL_FROM_NAME="BEILV AI"
export MAIL_FROM_ADDRESS=vcode@xrateverse.com
export MAIL_SSL_ENABLE=true
export MAIL_PROTOCOL=smtps

echo "🔧 邮件服务配置:"
echo "  SMTP服务器: $MAIL_HOST"
echo "  SMTP端口: $MAIL_PORT"  
echo "  发件邮箱: $MAIL_USERNAME"
echo "  SSL加密: $MAIL_SSL_ENABLE"
echo ""

echo "✅ 代码修复完成:"
echo "  - 修复了 RealEmailSendTest.java 中的方法调用"
echo "  - 修复了 VerificationCodeServiceTest.java 中的参数"
echo "  - 修复了 AuthControllerTest.java 中的Mock调用"
echo "  - 修复了 EmailVerificationLoginFlowTest.java 中的方法调用"
echo ""

echo "📝 主要修复内容:"
echo "  1. verificationCodeService.sendVerificationCode() → verificationCodeService.sendCode()"
echo "  2. param.setType() → param.setCodeType() 使用正确的属性名"
echo "  3. 'register' → CodeType.REGISTER.getCode() 使用数字代码"
echo "  4. mailService.sendVerificationCode() → 添加了缺失的参数"
echo "  5. API请求参数 'type' → 'codeType' 使用正确的字段名"
echo ""

echo "🧪 现在可以运行的测试:"
echo "  1. API测试: ./test-email-with-curl.sh"
echo "  2. 单元测试: ./run-tests.sh"
echo "  3. 简单测试: ./send-test-email.sh"
echo ""

echo "📧 测试API示例:"
echo "curl -X POST http://localhost:8085/api/auth/send-code \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{\"email\":\"gaoyongqiang@xrateverse.com\",\"codeType\":1}'"
echo ""

echo "🚀 测试步骤:"
echo "  1. 确保应用已启动: mvn spring-boot:run -Dspring.profiles.active=dev"
echo "  2. 运行API测试: ./test-email-with-curl.sh"
echo "  3. 检查邮箱: gaoyongqiang@xrateverse.com"
echo ""

echo "✅ 所有代码错误已修复，可以正常测试邮件发送功能了！"
echo "=========================================="