package com.macro.mall.portal.service.impl;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.portal.dto.CaseDetailResult;
import com.macro.mall.portal.dto.CaseListResult;
import com.macro.mall.portal.service.PortalCaseCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 门户端案例缓存服务单元测试
 * 测试所有缓存操作的正确性和异常处理
 */
@ExtendWith(MockitoExtension.class)
class PortalCaseCacheServiceImplTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private PortalCaseCacheServiceImpl portalCaseCacheService;

    private static final String REDIS_DATABASE = "test_mall";
    private static final String REDIS_KEY_CASE = "test_case";
    private static final Long REDIS_EXPIRE = 60L;
    private static final Long REDIS_EXPIRE_CASE = 30L;

    @BeforeEach
    void setUp() {
        // 注入配置值
        ReflectionTestUtils.setField(portalCaseCacheService, "REDIS_DATABASE", REDIS_DATABASE);
        ReflectionTestUtils.setField(portalCaseCacheService, "REDIS_KEY_CASE", REDIS_KEY_CASE);
        ReflectionTestUtils.setField(portalCaseCacheService, "REDIS_EXPIRE", REDIS_EXPIRE);
        ReflectionTestUtils.setField(portalCaseCacheService, "REDIS_EXPIRE_CASE", REDIS_EXPIRE_CASE);
    }

    @Test
    void testGetCaseCategoryList_Success() {
        // Given
        List<CaseCategory> expectedCategories = createTestCaseCategoryList();
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        when(redisService.get(expectedKey)).thenReturn(expectedCategories);

        // When
        List<CaseCategory> result = portalCaseCacheService.getCaseCategoryList();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("分类1", result.get(0).getName());
        assertEquals("分类2", result.get(1).getName());
        verify(redisService).get(expectedKey);
    }

    @Test
    void testGetCaseCategoryList_EmptyCache() {
        // Given
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        when(redisService.get(expectedKey)).thenReturn(null);

        // When
        List<CaseCategory> result = portalCaseCacheService.getCaseCategoryList();

        // Then
        assertNull(result);
        verify(redisService).get(expectedKey);
    }

    @Test
    void testSetCaseCategoryList_Success() {
        // Given
        List<CaseCategory> categories = createTestCaseCategoryList();
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";

        // When
        portalCaseCacheService.setCaseCategoryList(categories);

        // Then
        verify(redisService).set(expectedKey, categories, REDIS_EXPIRE);
    }

    @Test
    void testGetCaseDetail_Success() {
        // Given
        Long caseId = 1L;
        CaseDetailResult expectedDetail = createTestCaseDetail(caseId);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
        when(redisService.get(expectedKey)).thenReturn(expectedDetail);

        // When
        CaseDetailResult result = portalCaseCacheService.getCaseDetail(caseId);

        // Then
        assertNotNull(result);
        assertEquals(caseId, result.getId());
        assertEquals("测试案例", result.getTitle());
        verify(redisService).get(expectedKey);
    }

    @Test
    void testGetCaseDetail_EmptyCache() {
        // Given
        Long caseId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
        when(redisService.get(expectedKey)).thenReturn(null);

        // When
        CaseDetailResult result = portalCaseCacheService.getCaseDetail(caseId);

        // Then
        assertNull(result);
        verify(redisService).get(expectedKey);
    }

    @Test
    void testSetCaseDetail_Success() {
        // Given
        CaseDetailResult caseDetail = createTestCaseDetail(1L);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseDetail.getId();

        // When
        portalCaseCacheService.setCaseDetail(caseDetail);

        // Then
        verify(redisService).set(expectedKey, caseDetail, REDIS_EXPIRE_CASE);
    }

    @Test
    void testGetHotCaseList_Success() {
        // Given
        Integer size = 5;
        List<CaseListResult> expectedList = createTestCaseListResults(size);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot:" + size;
        when(redisService.get(expectedKey)).thenReturn(expectedList);

        // When
        List<CaseListResult> result = portalCaseCacheService.getHotCaseList(size);

        // Then
        assertNotNull(result);
        assertEquals(size, result.size());
        assertEquals("热门案例1", result.get(0).getTitle());
        verify(redisService).get(expectedKey);
    }

    @Test
    void testSetHotCaseList_Success() {
        // Given
        Integer size = 5;
        List<CaseListResult> hotCaseList = createTestCaseListResults(size);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot:" + size;

        // When
        portalCaseCacheService.setHotCaseList(hotCaseList, size);

        // Then
        verify(redisService).set(expectedKey, hotCaseList, REDIS_EXPIRE);
    }

    @Test
    void testGetLatestCaseList_Success() {
        // Given
        Integer size = 10;
        List<CaseListResult> expectedList = createTestCaseListResults(size);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest:" + size;
        when(redisService.get(expectedKey)).thenReturn(expectedList);

        // When
        List<CaseListResult> result = portalCaseCacheService.getLatestCaseList(size);

        // Then
        assertNotNull(result);
        assertEquals(size, result.size());
        assertEquals("热门案例1", result.get(0).getTitle());
        verify(redisService).get(expectedKey);
    }

    @Test
    void testSetLatestCaseList_Success() {
        // Given
        Integer size = 10;
        List<CaseListResult> latestCaseList = createTestCaseListResults(size);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest:" + size;

        // When
        portalCaseCacheService.setLatestCaseList(latestCaseList, size);

        // Then
        verify(redisService).set(expectedKey, latestCaseList, REDIS_EXPIRE);
    }

    @Test
    void testDelAllCache_Success() {
        // Given
        String categoryKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        String hotPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";
        String latestPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";
        String detailPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail*";

        // When
        portalCaseCacheService.delAllCache();

        // Then
        verify(redisService).del(categoryKey);
        verify(redisService).delByPattern(hotPattern);
        verify(redisService).delByPattern(latestPattern);
        verify(redisService).delByPattern(detailPattern);
    }

    @Test
    void testDelAllCache_WithException() {
        // Given
        String categoryKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        doThrow(new RuntimeException("Redis连接失败")).when(redisService).del(categoryKey);

        // When & Then - 不应该抛出异常，应该被捕获和记录
        assertDoesNotThrow(() -> portalCaseCacheService.delAllCache());
        verify(redisService).del(categoryKey);
    }

    @Test
    void testDelCaseCategoryCache_Success() {
        // Given
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";

        // When
        portalCaseCacheService.delCaseCategoryCache();

        // Then
        verify(redisService).del(expectedKey);
    }

    @Test
    void testDelCaseCategoryCache_WithException() {
        // Given
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category:list";
        doThrow(new RuntimeException("Redis连接失败")).when(redisService).del(expectedKey);

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> portalCaseCacheService.delCaseCategoryCache());
        verify(redisService).del(expectedKey);
    }

    @Test
    void testDelCaseDetailCache_Success() {
        // Given
        Long caseId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;

        // When
        portalCaseCacheService.delCaseDetailCache(caseId);

        // Then
        verify(redisService).del(expectedKey);
    }

    @Test
    void testDelCaseDetailCache_WithException() {
        // Given
        Long caseId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
        doThrow(new RuntimeException("Redis连接失败")).when(redisService).del(expectedKey);

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> portalCaseCacheService.delCaseDetailCache(caseId));
        verify(redisService).del(expectedKey);
    }

    @Test
    void testDelHotCaseCache_Success() {
        // Given
        String expectedPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";

        // When
        portalCaseCacheService.delHotCaseCache();

        // Then
        verify(redisService).delByPattern(expectedPattern);
    }

    @Test
    void testDelHotCaseCache_WithException() {
        // Given
        String expectedPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*";
        doThrow(new RuntimeException("Redis连接失败")).when(redisService).delByPattern(expectedPattern);

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> portalCaseCacheService.delHotCaseCache());
        verify(redisService).delByPattern(expectedPattern);
    }

    @Test
    void testDelLatestCaseCache_Success() {
        // Given
        String expectedPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";

        // When
        portalCaseCacheService.delLatestCaseCache();

        // Then
        verify(redisService).delByPattern(expectedPattern);
    }

    @Test
    void testDelLatestCaseCache_WithException() {
        // Given
        String expectedPattern = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*";
        doThrow(new RuntimeException("Redis连接失败")).when(redisService).delByPattern(expectedPattern);

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> portalCaseCacheService.delLatestCaseCache());
        verify(redisService).delByPattern(expectedPattern);
    }

    @Test
    void testKeyGeneration_Consistency() {
        // Given
        Long caseId = 123L;
        Integer size = 10;

        // When - 多次调用相同参数的方法
        portalCaseCacheService.getCaseDetail(caseId);
        portalCaseCacheService.delCaseDetailCache(caseId);
        portalCaseCacheService.getHotCaseList(size);

        // Then - 验证生成的key一致
        String expectedDetailKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail:" + caseId;
        String expectedHotKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot:" + size;

        verify(redisService, times(1)).get(expectedDetailKey);
        verify(redisService, times(1)).del(expectedDetailKey);
        verify(redisService, times(1)).get(expectedHotKey);
    }

    @Test
    void testCacheOperations_WithNullParameters() {
        // When & Then - 测试null参数处理
        assertDoesNotThrow(() -> {
            portalCaseCacheService.getCaseDetail(null);
            portalCaseCacheService.delCaseDetailCache(null);
            portalCaseCacheService.getHotCaseList(null);
            portalCaseCacheService.getLatestCaseList(null);
        });
    }

    @Test
    void testCacheOperations_WithEdgeCaseParameters() {
        // Given
        Long largeCaseId = Long.MAX_VALUE;
        Integer zeroSize = 0;
        Integer negativeSize = -1;

        // When & Then - 测试边界值参数
        assertDoesNotThrow(() -> {
            portalCaseCacheService.getCaseDetail(largeCaseId);
            portalCaseCacheService.delCaseDetailCache(largeCaseId);
            portalCaseCacheService.getHotCaseList(zeroSize);
            portalCaseCacheService.getLatestCaseList(negativeSize);
        });
    }

    // =================== 测试数据创建方法 ===================

    private List<CaseCategory> createTestCaseCategoryList() {
        CaseCategory category1 = new CaseCategory();
        category1.setId(1L);
        category1.setName("分类1");
        category1.setDescription("测试分类1");
        category1.setShowStatus(1);

        CaseCategory category2 = new CaseCategory();
        category2.setId(2L);
        category2.setName("分类2");
        category2.setDescription("测试分类2");
        category2.setShowStatus(1);

        return Arrays.asList(category1, category2);
    }

    private CaseDetailResult createTestCaseDetail(Long caseId) {
        CaseDetailResult detail = new CaseDetailResult();
        detail.setId(caseId);
        detail.setTitle("测试案例");
        detail.setContent("测试案例内容");
        detail.setCategoryId(1L);
        detail.setCategoryName("测试分类");
        detail.setImage("images/test-" + caseId + ".jpg");
        detail.setVideo("videos/test-" + caseId + ".mp4");
        detail.setStatus(1);
        detail.setShowStatus(1);
        return detail;
    }

    private List<CaseListResult> createTestCaseListResults(Integer size) {
        List<CaseListResult> results = new java.util.ArrayList<>();
        for (int i = 1; i <= size; i++) {
            CaseListResult result = new CaseListResult();
            result.setId((long) i);
            result.setTitle("热门案例" + i);
            result.setCategoryName("测试分类");
            result.setImage("images/case-" + i + ".jpg");
            result.setVideo("videos/case-" + i + ".mp4");
            result.setViewCount((long) (i * 100));
            result.setLikeCount((long) (i * 10));
            result.setHotScore(new BigDecimal(i * 1.5));
            results.add(result);
        }
        return results;
    }
}