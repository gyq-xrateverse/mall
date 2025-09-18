#!/bin/bash

# 案例模块测试脚本
# 用于统一运行所有案例相关的测试

echo "开始运行案例模块测试..."
echo "=================================="

# 进入项目目录
cd "$(dirname "$0")"

# 运行案例相关的单元测试
echo "1. 运行案例相关的单元测试..."
mvn test -Dtest="*Case*Test" -pl mall-admin

echo ""
echo "2. 运行缓存相关的测试..."
mvn test -Dtest="*Cache*Test" -pl mall-admin

echo ""
echo "3. 运行集成测试..."
mvn test -Dtest="*IntegrationTest" -pl mall-admin

echo ""
echo "4. 运行测试套件..."
mvn test -Dtest="AllCaseTestSuite" -pl mall-admin

echo ""
echo "5. 快速验证测试..."
mvn test -Dtest="AllCaseTestSuite#quickValidationTest" -pl mall-admin

echo ""
echo "=================================="
echo "案例模块测试完成！"

echo ""
echo "可用的测试命令："
echo "- 快速验证：mvn test -Dtest=\"AllCaseTestSuite#quickValidationTest\" -pl mall-admin"
echo "- 完整验证：mvn test -Dtest=\"AllCaseTestSuite#fullValidationTest\" -pl mall-admin"
echo "- 单元测试：mvn test -Dtest=\"AllCaseTestSuite\$UnitTests\" -pl mall-admin"
echo "- 边界测试：mvn test -Dtest=\"AllCaseTestSuite\$EdgeCaseTests\" -pl mall-admin"