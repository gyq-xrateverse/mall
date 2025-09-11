#!/bin/bash

# 使用curl调用API发送验证码邮件测试脚本
echo "=========================================="
echo "📧 BEILV AI 邮件API测试"
echo "目标邮箱: gaoyongqiang@xrateverse.com"
echo "=========================================="

# API端点
API_BASE_URL="http://localhost:8085/api/auth"
TARGET_EMAIL="gaoyongqiang@xrateverse.com"

echo "🔧 测试配置:"
echo "  API地址: $API_BASE_URL"
echo "  目标邮箱: $TARGET_EMAIL"
echo "  测试类型: 发送验证码API调用"
echo ""

# 检查curl是否可用
if ! command -v curl &> /dev/null; then
    echo "❌ curl未安装，无法执行API测试"
    exit 1
fi

echo "✅ curl已安装，开始测试"
echo ""

# 测试1: 发送注册验证码
echo "📧 测试1: 发送注册验证码"
echo "正在调用API..."

RESPONSE1=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$TARGET_EMAIL\",\"codeType\":1}" \
  "$API_BASE_URL/send-code" 2>/dev/null)

HTTP_STATUS1=$(echo "$RESPONSE1" | grep "HTTP_STATUS:" | cut -d: -f2)
BODY1=$(echo "$RESPONSE1" | grep -v "HTTP_STATUS:")

echo "响应状态码: $HTTP_STATUS1"
echo "响应内容: $BODY1"

if [ "$HTTP_STATUS1" = "200" ]; then
    echo "✅ 注册验证码发送请求成功"
else
    echo "❌ 注册验证码发送请求失败"
fi

echo ""

# 测试2: 发送密码重置验证码
echo "📧 测试2: 发送密码重置验证码"
echo "正在调用API..."

RESPONSE2=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X POST \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"$TARGET_EMAIL\",\"codeType\":3}" \
  "$API_BASE_URL/send-code" 2>/dev/null)

HTTP_STATUS2=$(echo "$RESPONSE2" | grep "HTTP_STATUS:" | cut -d: -f2)
BODY2=$(echo "$RESPONSE2" | grep -v "HTTP_STATUS:")

echo "响应状态码: $HTTP_STATUS2"
echo "响应内容: $BODY2"

if [ "$HTTP_STATUS2" = "200" ]; then
    echo "✅ 密码重置验证码发送请求成功"
else
    echo "❌ 密码重置验证码发送请求失败"
fi

echo ""

# 测试3: 检查邮箱是否存在
echo "📧 测试3: 检查邮箱存在性"
echo "正在调用API..."

RESPONSE3=$(curl -s -w "\nHTTP_STATUS:%{http_code}" \
  -X GET \
  "$API_BASE_URL/check-email?email=$TARGET_EMAIL" 2>/dev/null)

HTTP_STATUS3=$(echo "$RESPONSE3" | grep "HTTP_STATUS:" | cut -d: -f2)
BODY3=$(echo "$RESPONSE3" | grep -v "HTTP_STATUS:")

echo "响应状态码: $HTTP_STATUS3"
echo "响应内容: $BODY3"

if [ "$HTTP_STATUS3" = "200" ]; then
    echo "✅ 邮箱存在性检查成功"
else
    echo "❌ 邮箱存在性检查失败"
fi

echo ""
echo "=========================================="

# 总结测试结果
if [ "$HTTP_STATUS1" = "200" ] && [ "$HTTP_STATUS2" = "200" ] && [ "$HTTP_STATUS3" = "200" ]; then
    echo "✅ 邮件API测试全部通过！"
    echo ""
    echo "📮 请检查邮箱 $TARGET_EMAIL"
    echo "📁 注意查看垃圾邮件文件夹"
    echo "🕐 验证码有效期为5分钟"
    echo ""
    echo "您应该收到以下邮件:"
    echo "  1. 注册验证码邮件"
    echo "  2. 密码重置验证码邮件"
else
    echo "❌ 部分或全部API测试失败"
    echo ""
    echo "可能的原因:"
    echo "  - 服务器未启动 (请先启动 mall-portal 应用)"
    echo "  - 邮件服务配置错误"
    echo "  - 网络连接问题"
    echo "  - API端点地址错误"
    echo ""
    echo "建议:"
    echo "  1. 确保mall-portal应用已启动在8085端口"
    echo "  2. 检查邮件服务配置是否正确"
    echo "  3. 查看应用日志获取详细错误信息"
fi

echo "=========================================="

# 如果服务器未启动，提供启动建议
if ! curl -s "$API_BASE_URL/check-email?email=test@example.com" > /dev/null 2>&1; then
    echo ""
    echo "💡 服务器似乎未启动，请先启动mall-portal应用:"
    echo "   cd /mnt/d/software/beilv-agent/mall/mall/mall-portal"
    echo "   mvn spring-boot:run -Dspring.profiles.active=dev"
    echo ""
    echo "或者设置环境变量后启动:"
    echo "   export MAIL_HOST=smtp.exmail.qq.com"
    echo "   export MAIL_PORT=465"
    echo "   export MAIL_USERNAME=vcode@xrateverse.com"
    echo "   export MAIL_PASSWORD=NNyqSi67bLuGLzpv"
    echo "   export MAIL_SSL_ENABLE=true"
    echo "   mvn spring-boot:run -Dspring.profiles.active=dev"
fi