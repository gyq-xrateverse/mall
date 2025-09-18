#!/bin/bash

# 验证所有测试方法都是public的脚本

echo "验证测试方法可见性..."
echo "=================================="

# 查找所有测试文件
test_files=$(find /mnt/d/software/beilv-agent/mall/mall/mall-admin/src/test/java -name "*Test*.java")

echo "检查的测试文件数量: $(echo "$test_files" | wc -l)"
echo ""

# 检查是否有非public的测试方法
non_public_methods=0

for file in $test_files; do
    # 查找 @Test 注解后没有 public 的方法
    if grep -Pzo "(?s)@Test.*?\n\s*void\s+test" "$file" > /dev/null 2>&1; then
        echo "❌ 发现非public测试方法: $file"
        grep -n -A1 "@Test" "$file" | grep "void test" | head -3
        ((non_public_methods++))
    fi
done

echo ""
if [ $non_public_methods -eq 0 ]; then
    echo "✅ 所有测试方法都是public！"
    echo ""
    echo "示例验证："
    echo "- 检查CacheKeyConstantsTest前3个方法："
    grep -n "public void test" /mnt/d/software/beilv-agent/mall/mall/mall-admin/src/test/java/com/macro/mall/common/constant/CacheKeyConstantsTest.java | head -3
    echo ""
    echo "- 检查CacheSyncIntegrationTest前3个方法："
    grep -n "public void test" /mnt/d/software/beilv-agent/mall/mall/mall-admin/src/test/java/com/macro/mall/integration/CacheSyncIntegrationTest.java | head -3
else
    echo "❌ 发现 $non_public_methods 个文件包含非public测试方法"
fi

echo ""
echo "=================================="
echo "验证完成！"