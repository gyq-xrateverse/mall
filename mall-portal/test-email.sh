#!/bin/bash

echo "=== 邮件服务商测试脚本 ==="

# 测试函数
test_email_config() {
    local provider=$1
    local host=$2
    local port=$3
    local username=$4
    local password=$5
    local protocol=$6
    local ssl_enable=$7
    local starttls_enable=$8
    
    echo "测试 $provider 配置..."
    echo "Host: $host, Port: $port, Protocol: $protocol"
    
    # 设置环境变量
    export MAIL_HOST=$host
    export MAIL_PORT=$port
    export MAIL_USERNAME=$username
    export MAIL_PASSWORD=$password
    export MAIL_PROTOCOL=$protocol
    export MAIL_SSL_ENABLE=$ssl_enable
    export MAIL_STARTTLS_ENABLE=$starttls_enable
    
    # 测试邮件发送
    response=$(curl -s -X POST http://localhost:8085/api/auth/send-code \
        -H "Content-Type: application/json" \
        -d '{"email":"test@example.com","codeType":2}')
    
    echo "Response: $response"
    echo "---"
}

# 测试不同的邮件服务商配置
echo "开始测试不同邮件服务商配置..."

# 测试1: 腾讯企业邮箱 (默认配置)
test_email_config "腾讯企业邮箱" \
    "smtp.exmail.qq.com" \
    "465" \
    "vcode@xrateverse.com" \
    "NNyqSi67bLuGLzpv" \
    "smtps" \
    "true" \
    "false"

# 测试2: QQ邮箱配置
test_email_config "QQ邮箱" \
    "smtp.qq.com" \
    "587" \
    "vcode@xrateverse.com" \
    "NNyqSi67bLuGLzpv" \
    "smtp" \
    "false" \
    "true"

# 测试3: 网易163邮箱
test_email_config "网易163" \
    "smtp.163.com" \
    "25" \
    "vcode@xrateverse.com" \
    "NNyqSi67bLuGLzpv" \
    "smtp" \
    "false" \
    "false"

echo "测试完成！"