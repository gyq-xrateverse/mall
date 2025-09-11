#!/bin/bash

echo "=== 邮件服务商快速切换脚本 ==="

# 检查参数
if [ $# -eq 0 ]; then
    echo "用法: $0 [provider] [email] [password]"
    echo "可用的邮件服务商:"
    echo "  163     - 网易163邮箱 (推荐测试)"
    echo "  qq      - QQ邮箱"
    echo "  gmail   - Gmail"
    echo "  outlook - Outlook"
    echo ""
    echo "示例:"
    echo "  $0 163 your@163.com your-auth-code"
    echo "  $0 qq your@qq.com your-qq-auth-code"
    exit 1
fi

PROVIDER=$1
EMAIL=$2
PASSWORD=$3

# 检查必需参数
if [ -z "$EMAIL" ] || [ -z "$PASSWORD" ]; then
    echo "错误: 请提供邮箱和密码/授权码"
    exit 1
fi

echo "切换到邮件服务商: $PROVIDER"
echo "邮箱: $EMAIL"
echo "密码: [隐藏]"

case $PROVIDER in
    "163")
        echo "配置网易163邮箱..."
        export MAIL_HOST=smtp.163.com
        export MAIL_PORT=25
        export MAIL_USERNAME=$EMAIL
        export MAIL_PASSWORD=$PASSWORD
        export MAIL_FROM_ADDRESS=$EMAIL
        export MAIL_FROM_NAME="BEILV AI"
        export MAIL_FROM="BEILV AI <$EMAIL>"
        export MAIL_PROTOCOL=smtp
        export MAIL_SSL_ENABLE=false
        export MAIL_STARTTLS_ENABLE=false
        export MAIL_DEBUG=true
        ;;
    "qq")
        echo "配置QQ邮箱..."
        export MAIL_HOST=smtp.qq.com
        export MAIL_PORT=587
        export MAIL_USERNAME=$EMAIL
        export MAIL_PASSWORD=$PASSWORD
        export MAIL_FROM_ADDRESS=$EMAIL
        export MAIL_FROM_NAME="BEILV AI"
        export MAIL_FROM="BEILV AI <$EMAIL>"
        export MAIL_PROTOCOL=smtp
        export MAIL_SSL_ENABLE=false
        export MAIL_STARTTLS_ENABLE=true
        export MAIL_STARTTLS_REQUIRED=true
        export MAIL_DEBUG=true
        ;;
    "gmail")
        echo "配置Gmail..."
        export MAIL_HOST=smtp.gmail.com
        export MAIL_PORT=587
        export MAIL_USERNAME=$EMAIL
        export MAIL_PASSWORD=$PASSWORD
        export MAIL_FROM_ADDRESS=$EMAIL
        export MAIL_FROM_NAME="BEILV AI"
        export MAIL_FROM="BEILV AI <$EMAIL>"
        export MAIL_PROTOCOL=smtp
        export MAIL_SSL_ENABLE=false
        export MAIL_STARTTLS_ENABLE=true
        export MAIL_STARTTLS_REQUIRED=true
        export MAIL_DEBUG=true
        ;;
    "outlook")
        echo "配置Outlook..."
        export MAIL_HOST=smtp-mail.outlook.com
        export MAIL_PORT=587
        export MAIL_USERNAME=$EMAIL
        export MAIL_PASSWORD=$PASSWORD
        export MAIL_FROM_ADDRESS=$EMAIL
        export MAIL_FROM_NAME="BEILV AI"
        export MAIL_FROM="BEILV AI <$EMAIL>"
        export MAIL_PROTOCOL=smtp
        export MAIL_SSL_ENABLE=false
        export MAIL_STARTTLS_ENABLE=true
        export MAIL_STARTTLS_REQUIRED=true
        export MAIL_DEBUG=true
        ;;
    *)
        echo "错误: 不支持的邮件服务商 '$PROVIDER'"
        echo "支持的服务商: 163, qq, gmail, outlook"
        exit 1
        ;;
esac

echo ""
echo "✅ 邮件配置已更新!"
echo "当前环境变量:"
echo "  MAIL_HOST=$MAIL_HOST"
echo "  MAIL_PORT=$MAIL_PORT"
echo "  MAIL_USERNAME=$MAIL_USERNAME"
echo "  MAIL_PROTOCOL=$MAIL_PROTOCOL"
echo ""
echo "现在重启你的Spring Boot应用或直接测试发送邮件:"
echo "curl -X POST http://localhost:8085/api/auth/send-code -H \"Content-Type: application/json\" -d '{\"email\":\"test@example.com\",\"codeType\":2}'"