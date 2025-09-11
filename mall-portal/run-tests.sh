#!/bin/bash

# 邮箱验证登录功能测试脚本
echo "=========================================="
echo "邮箱验证登录功能测试套件"
echo "=========================================="

# 设置测试环境
export SPRING_PROFILES_ACTIVE=test

# 检查Java和Maven是否可用
echo "检查测试环境..."

if ! command -v java &> /dev/null; then
    echo "❌ Java未安装或未在PATH中"
    exit 1
fi

echo "✅ Java版本: $(java -version 2>&1 | head -1)"

# 如果Maven不可用，尝试使用项目中的Maven Wrapper
if command -v mvn &> /dev/null; then
    MVN_CMD="mvn"
elif [ -f "./mvnw" ]; then
    MVN_CMD="./mvnw"
    chmod +x ./mvnw
else
    echo "❌ Maven未找到，请安装Maven或确保./mvnw存在"
    exit 1
fi

echo "✅ Maven命令: $MVN_CMD"

# 编译项目
echo ""
echo "📦 编译项目..."
$MVN_CMD clean compile test-compile -q

if [ $? -ne 0 ]; then
    echo "❌ 项目编译失败"
    exit 1
fi

echo "✅ 项目编译成功"

# 运行单元测试
echo ""
echo "🧪 运行邮件服务单元测试..."
$MVN_CMD test -Dtest=MailServiceTest -q

echo ""
echo "🧪 运行验证码服务单元测试..."
$MVN_CMD test -Dtest=VerificationCodeServiceTest -q

echo ""
echo "🧪 运行认证控制器集成测试..."
$MVN_CMD test -Dtest=AuthControllerTest -q

echo ""
echo "🧪 运行完整流程集成测试..."
$MVN_CMD test -Dtest=EmailVerificationLoginFlowTest -q

# 运行所有相关测试
echo ""
echo "🚀 运行完整测试套件..."
$MVN_CMD test -Dtest="**/service/*Test,**/controller/*Test,**/integration/*Test" -q

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "✅ 所有测试通过！邮箱验证登录功能正常"
    echo "=========================================="
else
    echo ""
    echo "=========================================="
    echo "❌ 测试失败，请查看上方错误信息"
    echo "=========================================="
    exit 1
fi

# 生成测试报告
echo ""
echo "📊 生成测试报告..."
$MVN_CMD surefire-report:report -q

echo "📋 测试报告生成在: target/site/surefire-report.html"

echo ""
echo "🎉 邮箱验证登录功能测试完成！"