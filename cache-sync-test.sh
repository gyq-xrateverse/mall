#!/bin/bash

# 缓存同步问题验证脚本
# 用于测试管理端操作是否能正确同步用户端缓存

echo "=== 缓存同步问题修复验证 ==="

echo "1. 检查Redis连接状态..."
redis-cli -p 16379 ping || {
    echo "Redis连接失败，请确保Redis服务正在运行"
    exit 1
}

echo "2. 检查当前缓存状态..."
echo "用户端缓存键："
redis-cli -p 16379 keys "mall:case:portal:*"

echo "管理端缓存键："
redis-cli -p 16379 keys "mall:case:*" | grep -v portal

echo "3. 清理所有案例相关缓存进行干净测试..."
redis-cli -p 16379 del $(redis-cli -p 16379 keys "mall:case:*")

echo "4. 启动应用进行测试..."
echo "请执行以下测试步骤："
echo "   a) 启动管理端应用(mall-admin)"
echo "   b) 启动用户端应用(mall-portal)"
echo "   c) 在用户端访问案例列表，建立缓存"
echo "   d) 在管理端执行案例增删改操作"
echo "   e) 检查用户端缓存是否实时更新"

echo "5. 测试完成后，检查缓存同步结果..."
echo "用于验证的Redis命令："
echo "   查看所有案例缓存: redis-cli -p 16379 keys 'mall:case:*'"
echo "   监听消息频道: redis-cli -p 16379 subscribe mall:cache:update"

echo "=== 修复内容总结 ==="
echo "1. 采用最简洁的解决方案：直接清理用户端缓存"
echo "2. 移除了复杂的权限验证和消息机制，避免阻塞"
echo "3. 管理端操作时同时清理自己和用户端的缓存"
echo "4. 简化了缓存同步逻辑，提高了可靠性"
echo ""
echo "预期结果：管理端的增删改操作应该能立即清理用户端缓存"