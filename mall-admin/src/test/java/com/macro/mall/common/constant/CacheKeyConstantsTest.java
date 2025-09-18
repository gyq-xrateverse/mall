package com.macro.mall.common.constant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存键常量测试
 * 测试缓存键生成规则、键名格式验证和键构建方法
 */
public class CacheKeyConstantsTest {

    @Test
    public void testCacheKeyConstants_ShouldHaveCorrectValues() {
        // Given & When & Then - 验证常量值
        assertEquals("mall:cache:update", CacheKeyConstants.CACHE_UPDATE_CHANNEL);
        assertEquals("mall:case:admin:", CacheKeyConstants.ADMIN_CASE_PREFIX);
        assertEquals("mall:case:portal:", CacheKeyConstants.PORTAL_CASE_PREFIX);
    }

    @Test
    public void testBuildAdminCaseKey_WithSingleParameter_ShouldGenerateCorrectKey() {
        // Given
        String baseKey = "category";
        String param = "list";

        // When
        String result = CacheKeyConstants.buildAdminCaseKey(baseKey, param);

        // Then
        assertEquals("mall:case:admin:category:list", result);
    }

    @Test
    public void testBuildAdminCaseKey_WithMultipleParameters_ShouldGenerateCorrectKey() {
        // Given
        String baseKey = "detail";
        Object param1 = 123L;
        Object param2 = "user456";
        Object param3 = "version1";

        // When
        String result = CacheKeyConstants.buildAdminCaseKey(baseKey, param1, param2, param3);

        // Then
        assertEquals("mall:case:admin:detail:123:user456:version1", result);
    }

    @Test
    public void testBuildAdminCaseKey_WithNoParameters_ShouldGenerateBaseKey() {
        // Given
        String baseKey = "statistics";

        // When
        String result = CacheKeyConstants.buildAdminCaseKey(baseKey);

        // Then
        assertEquals("mall:case:admin:statistics", result);
    }

    @Test
    public void testBuildAdminCaseKey_WithNullParameters_ShouldHandleGracefully() {
        // Given
        String baseKey = "test";
        Object nullParam = null;
        String validParam = "valid";

        // When
        String result = CacheKeyConstants.buildAdminCaseKey(baseKey, nullParam, validParam);

        // Then
        assertEquals("mall:case:admin:test:null:valid", result);
    }

    @Test
    public void testBuildAdminCaseKey_WithEmptyString_ShouldIncludeEmptyString() {
        // Given
        String baseKey = "test";
        String emptyParam = "";
        String validParam = "valid";

        // When
        String result = CacheKeyConstants.buildAdminCaseKey(baseKey, emptyParam, validParam);

        // Then
        assertEquals("mall:case:admin:test::valid", result);
    }

    @Test
    public void testBuildPortalCaseKey_WithSingleParameter_ShouldGenerateCorrectKey() {
        // Given
        String baseKey = "hot";
        Integer param = 10;

        // When
        String result = CacheKeyConstants.buildPortalCaseKey(baseKey, param);

        // Then
        assertEquals("mall:case:portal:hot:10", result);
    }

    @Test
    public void testBuildPortalCaseKey_WithMultipleParameters_ShouldGenerateCorrectKey() {
        // Given
        String baseKey = "latest";
        Object param1 = 5;
        Object param2 = "category_tech";

        // When
        String result = CacheKeyConstants.buildPortalCaseKey(baseKey, param1, param2);

        // Then
        assertEquals("mall:case:portal:latest:5:category_tech", result);
    }

    @Test
    public void testKeyBuilders_WithSpecialCharacters_ShouldHandleCorrectly() {
        // Given
        String baseKey = "special";
        String paramWithColon = "param:with:colon";
        String paramWithSpace = "param with space";
        String paramWithSlash = "param/with/slash";

        // When
        String adminKey = CacheKeyConstants.buildAdminCaseKey(baseKey, paramWithColon, paramWithSpace, paramWithSlash);
        String portalKey = CacheKeyConstants.buildPortalCaseKey(baseKey, paramWithColon);

        // Then
        assertEquals("mall:case:admin:special:param:with:colon:param with space:param/with/slash", adminKey);
        assertEquals("mall:case:portal:special:param:with:colon", portalKey);
    }

    @Test
    public void testKeyBuilders_WithNumericTypes_ShouldConvertCorrectly() {
        // Given
        String baseKey = "numeric";
        Integer intParam = 123;
        Long longParam = 456789L;
        Double doubleParam = 123.45;
        Boolean boolParam = true;

        // When
        String adminKey = CacheKeyConstants.buildAdminCaseKey(baseKey, intParam, longParam, doubleParam, boolParam);

        // Then
        assertEquals("mall:case:admin:numeric:123:456789:123.45:true", adminKey);
    }

    @Test
    public void testKeyBuilders_Consistency_SameParametersShouldProduceSameKeys() {
        // Given
        String baseKey = "consistency";
        Object param1 = "test";
        Object param2 = 123;

        // When
        String key1 = CacheKeyConstants.buildAdminCaseKey(baseKey, param1, param2);
        String key2 = CacheKeyConstants.buildAdminCaseKey(baseKey, param1, param2);
        String key3 = CacheKeyConstants.buildAdminCaseKey(baseKey, "test", 123);

        // Then
        assertEquals(key1, key2);
        assertEquals(key1, key3);
    }

    @Test
    public void testKeyBuilders_Uniqueness_DifferentParametersShouldProduceDifferentKeys() {
        // Given
        String baseKey = "uniqueness";

        // When
        String key1 = CacheKeyConstants.buildAdminCaseKey(baseKey, "param1");
        String key2 = CacheKeyConstants.buildAdminCaseKey(baseKey, "param2");
        String key3 = CacheKeyConstants.buildAdminCaseKey(baseKey, "param1", "param2");

        // Then
        assertNotEquals(key1, key2);
        assertNotEquals(key1, key3);
        assertNotEquals(key2, key3);
    }

    @Test
    public void testKeyBuilders_WithEmptyBaseKey_ShouldHandleCorrectly() {
        // Given
        String emptyBaseKey = "";
        String param = "test";

        // When
        String adminKey = CacheKeyConstants.buildAdminCaseKey(emptyBaseKey, param);
        String portalKey = CacheKeyConstants.buildPortalCaseKey(emptyBaseKey, param);

        // Then
        assertEquals("mall:case:admin::test", adminKey);
        assertEquals("mall:case:portal::test", portalKey);
    }

    @Test
    public void testKeyBuilders_Performance_ShouldBeEfficient() {
        // Given
        String baseKey = "performance";
        String param = "test";
        long startTime = System.nanoTime();

        // When - 执行大量键生成操作
        for (int i = 0; i < 10000; i++) {
            CacheKeyConstants.buildAdminCaseKey(baseKey, param, i);
            CacheKeyConstants.buildPortalCaseKey(baseKey, param, i);
        }

        long endTime = System.nanoTime();
        long duration = endTime - startTime;

        // Then - 应该在合理时间内完成（1秒以内）
        assertTrue(duration < 1_000_000_000L, "键生成操作应该高效完成");
    }

    @Test
    public void testConstantValues_ShouldNotBeEmpty() {
        // When & Then
        assertNotNull(CacheKeyConstants.CACHE_UPDATE_CHANNEL);
        assertNotNull(CacheKeyConstants.ADMIN_CASE_PREFIX);
        assertNotNull(CacheKeyConstants.PORTAL_CASE_PREFIX);

        assertFalse(CacheKeyConstants.CACHE_UPDATE_CHANNEL.isEmpty());
        assertFalse(CacheKeyConstants.ADMIN_CASE_PREFIX.isEmpty());
        assertFalse(CacheKeyConstants.PORTAL_CASE_PREFIX.isEmpty());
    }

    @Test
    public void testConstantValues_ShouldFollowNamingConvention() {
        // When & Then - 验证常量遵循预期的命名规范
        assertTrue(CacheKeyConstants.CACHE_UPDATE_CHANNEL.contains("cache"));
        assertTrue(CacheKeyConstants.ADMIN_CASE_PREFIX.contains("admin"));
        assertTrue(CacheKeyConstants.PORTAL_CASE_PREFIX.contains("portal"));
    }

    @Test
    public void testKeyBuilders_WithVeryLongParameters_ShouldHandleCorrectly() {
        // Given
        String baseKey = "long";
        StringBuilder longParam = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longParam.append("a");
        }

        // When
        String result = CacheKeyConstants.buildAdminCaseKey(baseKey, longParam.toString());

        // Then
        assertNotNull(result);
        assertTrue(result.startsWith(CacheKeyConstants.ADMIN_CASE_PREFIX + baseKey + ":"));
        assertTrue(result.length() > 1000);
    }

    // 测试具体的获取键方法
    @Test
    public void testGetAdminCaseCategoryKey_ShouldGenerateCorrectKey() {
        // Given
        Long categoryId = 123L;

        // When
        String result = CacheKeyConstants.getAdminCaseCategoryKey(categoryId);

        // Then
        assertEquals("mall:case:admin:category:123", result);
    }

    @Test
    public void testGetAdminCaseCategoryListKey_ShouldGenerateCorrectKey() {
        // When
        String result = CacheKeyConstants.getAdminCaseCategoryListKey();

        // Then
        assertEquals("mall:case:admin:category:list", result);
    }

    @Test
    public void testGetAdminCaseDataKey_ShouldGenerateCorrectKey() {
        // Given
        Long caseId = 456L;

        // When
        String result = CacheKeyConstants.getAdminCaseDataKey(caseId);

        // Then
        assertEquals("mall:case:admin:data:456", result);
    }

    @Test
    public void testGetAdminCaseDataHotKey_ShouldGenerateCorrectKey() {
        // When
        String result = CacheKeyConstants.getAdminCaseDataHotKey();

        // Then
        assertEquals("mall:case:admin:data:hot", result);
    }

    @Test
    public void testGetAdminCaseDataLatestKey_ShouldGenerateCorrectKey() {
        // When
        String result = CacheKeyConstants.getAdminCaseDataLatestKey();

        // Then
        assertEquals("mall:case:admin:data:latest", result);
    }

    @Test
    public void testGetPortalCaseCategoryListKey_ShouldGenerateCorrectKey() {
        // When
        String result = CacheKeyConstants.getPortalCaseCategoryListKey();

        // Then
        assertEquals("mall:case:portal:category:list", result);
    }

    @Test
    public void testGetPortalCaseDetailKey_ShouldGenerateCorrectKey() {
        // Given
        Long caseId = 789L;

        // When
        String result = CacheKeyConstants.getPortalCaseDetailKey(caseId);

        // Then
        assertEquals("mall:case:portal:detail:789", result);
    }

    @Test
    public void testGetPortalCaseHotKey_ShouldGenerateCorrectKey() {
        // Given
        Integer size = 10;

        // When
        String result = CacheKeyConstants.getPortalCaseHotKey(size);

        // Then
        assertEquals("mall:case:portal:hot:10", result);
    }

    @Test
    public void testGetPortalCaseLatestKey_ShouldGenerateCorrectKey() {
        // Given
        Integer size = 20;

        // When
        String result = CacheKeyConstants.getPortalCaseLatestKey(size);

        // Then
        assertEquals("mall:case:portal:latest:20", result);
    }

    @Test
    public void testGetPortalCaseHotPattern_ShouldGenerateCorrectPattern() {
        // When
        String result = CacheKeyConstants.getPortalCaseHotPattern();

        // Then
        assertEquals("mall:case:portal:hot:*", result);
    }

    @Test
    public void testGetPortalCaseLatestPattern_ShouldGenerateCorrectPattern() {
        // When
        String result = CacheKeyConstants.getPortalCaseLatestPattern();

        // Then
        assertEquals("mall:case:portal:latest:*", result);
    }
}
