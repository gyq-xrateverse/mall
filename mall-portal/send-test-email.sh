#!/bin/bash

# 真实邮件发送测试脚本
echo "=========================================="
echo "📧 BEILV AI 邮件服务测试"
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

# 检查Java和Maven
if ! command -v java &> /dev/null; then
    echo "❌ Java未安装或未在PATH中"
    exit 1
fi

# 选择Maven命令
if command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
elif [ -f "./mvnw" ]; then
    MVN_CMD="./mvnw"
    chmod +x ./mvnw
else
    echo "❌ Maven未找到，请安装Maven或确保./mvnw存在"
    exit 1
fi

echo "✅ 使用Maven命令: $MVN_CMD"
echo ""

# 编译项目
echo "📦 编译项目..."
$MVN_CMD clean compile test-compile -q

if [ $? -ne 0 ]; then
    echo "❌ 项目编译失败"
    exit 1
fi

echo "✅ 项目编译成功"
echo ""

# 运行邮件发送测试
echo "🚀 开始执行邮件发送测试..."
echo ""

echo "📧 测试1: 发送注册验证码邮件"
$MVN_CMD test -Dtest=RealEmailSendTest#testSendRegisterVerificationCode -q

echo ""
echo "📧 测试2: 直接邮件服务发送测试"
$MVN_CMD test -Dtest=RealEmailSendTest#testDirectMailServiceSend -q

echo ""
echo "📧 测试3: 发送密码重置验证码"
$MVN_CMD test -Dtest=RealEmailSendTest#testSendPasswordResetCode -q

echo ""
echo "📧 测试4: 发送HTML格式邮件"
$MVN_CMD test -Dtest=RealEmailSendTest#testSendHtmlEmail -q

echo ""
echo "📧 测试5: 邮件服务器连接测试"
$MVN_CMD test -Dtest=RealEmailSendTest#testMailConnectionConfiguration -q

echo ""
echo "=========================================="

if [ $? -eq 0 ]; then
    echo "✅ 邮件发送测试完成！"
    echo ""
    echo "📮 请检查邮箱 gaoyongqiang@xrateverse.com"
    echo "📁 注意查看垃圾邮件文件夹"
    echo "🕐 验证码有效期为5分钟"
    echo ""
    echo "📧 您应该收到以下邮件:"
    echo "  1. 注册验证码邮件"
    echo "  2. 测试验证码邮件 (验证码: 888888)"
    echo "  3. 密码重置验证码邮件"
    echo "  4. HTML格式测试邮件"
    echo "  5. 连接测试邮件"
else
    echo "❌ 邮件发送测试失败"
    echo ""
    echo "可能的原因:"
    echo "  - 邮件服务器配置错误"
    echo "  - 网络连接问题"
    echo "  - 邮箱认证失败"
    echo "  - 目标邮箱地址无效"
fi

echo "=========================================="