package com.macro.mall.suite;

import com.macro.mall.common.constant.CacheKeyConstantsTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * 案例相关测试套件
 * 统一运行所有案例相关的单元测试、集成测试和缓存测试
 */
@DisplayName("案例模块测试套件")
public class CaseTestSuite {

    /**
     * 运行缓存键常量相关的所有测试
     */
    @Test
    @DisplayName("缓存键常量测试")
    public void runCacheKeyConstantsTests() {
        CacheKeyConstantsTest test = new CacheKeyConstantsTest();

        // 基础常量测试
        test.testCacheKeyConstants_ShouldHaveCorrectValues();
        test.testConstantValues_ShouldNotBeEmpty();
        test.testConstantValues_ShouldFollowNamingConvention();

        // 键构建测试
        test.testBuildAdminCaseKey_WithSingleParameter_ShouldGenerateCorrectKey();
        test.testBuildPortalCaseKey_WithSingleParameter_ShouldGenerateCorrectKey();

        // 具体方法测试
        test.testGetAdminCaseCategoryKey_ShouldGenerateCorrectKey();
        test.testGetPortalCaseDetailKey_ShouldGenerateCorrectKey();
    }
}