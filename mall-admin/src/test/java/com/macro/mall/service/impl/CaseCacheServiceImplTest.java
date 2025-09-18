package com.macro.mall.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.constant.CacheKeyConstants;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.component.CacheSecurityGuard;
import com.macro.mall.dto.CacheUpdateMessage;
import com.macro.mall.model.CaseCategory;
import com.macro.mall.model.CaseData;
import com.macro.mall.test.config.CacheTestConfig;
import com.macro.mall.test.util.CacheTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CaseCacheServiceImpl 单元测试
 */
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = CacheTestConfig.class)
public class CaseCacheServiceImplTest {

    @Mock
    private RedisService redisService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CacheSecurityGuard cacheSecurityGuard;

    @InjectMocks
    private CaseCacheServiceImpl caseCacheService;

    private static final String REDIS_DATABASE = "test_mall";
    private static final String REDIS_KEY_CASE = "test_case";
    private static final Long REDIS_EXPIRE = 3600L;
    private static final Long REDIS_EXPIRE_CASE = 1800L;

    @BeforeEach
    public void setUp() {
        // 设置配置值
        ReflectionTestUtils.setField(caseCacheService, "REDIS_DATABASE", REDIS_DATABASE);
        ReflectionTestUtils.setField(caseCacheService, "REDIS_KEY_CASE", REDIS_KEY_CASE);
        ReflectionTestUtils.setField(caseCacheService, "REDIS_EXPIRE", REDIS_EXPIRE);
        ReflectionTestUtils.setField(caseCacheService, "REDIS_EXPIRE_CASE", REDIS_EXPIRE_CASE);
    }

    @Test
    public void testDelCaseCategory() {
        // Given
        Long categoryId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:" + categoryId;

        // When
        caseCacheService.delCaseCategory(categoryId);

        // Then
        verify(redisService, times(1)).del(expectedKey);
    }

    @Test
    public void testDelCaseCategoryList() {
        // Given
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:list";

        // When
        caseCacheService.delCaseCategoryList();

        // Then
        verify(redisService, times(1)).del(expectedKey);
    }

    @Test
    public void testGetCaseCategory() {
        // Given
        Long categoryId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:" + categoryId;
        CaseCategory expectedCategory = CacheTestData.createTestCaseCategory(categoryId);

        when(redisService.get(expectedKey)).thenReturn(expectedCategory);

        // When
        CaseCategory result = caseCacheService.getCaseCategory(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCategory.getId(), result.getId());
        assertEquals(expectedCategory.getName(), result.getName());
        verify(redisService, times(1)).get(expectedKey);
    }

    @Test
    public void testSetCaseCategory() {
        // Given
        CaseCategory category = CacheTestData.createTestCaseCategory(1L);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category:" + category.getId();

        // When
        caseCacheService.setCaseCategory(category);

        // Then
        verify(redisService, times(1)).set(expectedKey, category, REDIS_EXPIRE);
    }

    @Test
    public void testDelCaseData() {
        // Given
        Long caseId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:" + caseId;

        // When
        caseCacheService.delCaseData(caseId);

        // Then
        verify(redisService, times(1)).del(expectedKey);
    }

    @Test
    public void testGetCaseData() {
        // Given
        Long caseId = 1L;
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:" + caseId;
        CaseData expectedCaseData = CacheTestData.createTestCaseData(caseId);

        when(redisService.get(expectedKey)).thenReturn(expectedCaseData);

        // When
        CaseData result = caseCacheService.getCaseData(caseId);

        // Then
        assertNotNull(result);
        assertEquals(expectedCaseData.getId(), result.getId());
        assertEquals(expectedCaseData.getTitle(), result.getTitle());
        verify(redisService, times(1)).get(expectedKey);
    }

    @Test
    public void testSetCaseData() {
        // Given
        CaseData caseData = CacheTestData.createTestCaseData(1L);
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:" + caseData.getId();

        // When
        caseCacheService.setCaseData(caseData);

        // Then
        verify(redisService, times(1)).set(expectedKey, caseData, REDIS_EXPIRE_CASE);
    }

    @Test
    public void testBatchClearCaseCache() {
        // Given
        Long caseId = 1L;
        String operator = CacheTestData.createTestOperator();

        // When
        caseCacheService.batchClearCaseCache(caseId, operator);

        // Then
        verify(redisService, times(1)).del(anyString());
    }

    @Test
    public void testBatchClearCaseListCache() {
        // Given
        String operator = CacheTestData.createTestOperator();

        // When
        caseCacheService.batchClearCaseListCache(operator);

        // Then
        verify(redisService, times(4)).del(anyString()); // 4个缓存键
    }

    @Test
    public void testPublishCacheUpdateMessage_Success() throws Exception {
        // Given
        CacheUpdateMessage message = CacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            "test_user"
        );
        String messageJson = "{\"test\":\"message\"}";

        when(cacheSecurityGuard.checkPermission(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(true);
        when(objectMapper.writeValueAsString(message)).thenReturn(messageJson);
        when(cacheSecurityGuard.validateMessageSource(messageJson)).thenReturn(true);

        // When
        caseCacheService.publishCacheUpdateMessage(message);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(message.getOperator(), "MESSAGE_PUBLISH");
        verify(cacheSecurityGuard, times(1)).checkRateLimit(message.getOperator(), "MESSAGE_PUBLISH");
        verify(objectMapper, times(1)).writeValueAsString(message);
        verify(cacheSecurityGuard, times(1)).validateMessageSource(messageJson);
        verify(redisTemplate, times(1)).convertAndSend(CacheKeyConstants.CACHE_UPDATE_CHANNEL, messageJson);
    }

    @Test
    public void testPublishCacheUpdateMessage_PermissionDenied() throws Exception {
        // Given
        CacheUpdateMessage message = CacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            "test_user"
        );

        when(cacheSecurityGuard.checkPermission(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(false);

        // When
        caseCacheService.publishCacheUpdateMessage(message);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(message.getOperator(), "MESSAGE_PUBLISH");
        verify(cacheSecurityGuard, times(1)).logSecurityEvent(
            eq(message.getOperator()),
            eq("MESSAGE_PUBLISH"),
            eq("PERMISSION_DENIED"),
            anyString()
        );
        verify(objectMapper, never()).writeValueAsString(any());
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }

    @Test
    public void testPublishCacheUpdateMessage_RateLimitExceeded() throws Exception {
        // Given
        CacheUpdateMessage message = CacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            "test_user"
        );

        when(cacheSecurityGuard.checkPermission(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(false);

        // When
        caseCacheService.publishCacheUpdateMessage(message);

        // Then
        verify(cacheSecurityGuard, times(1)).checkRateLimit(message.getOperator(), "MESSAGE_PUBLISH");
        verify(cacheSecurityGuard, times(1)).logSecurityEvent(
            eq(message.getOperator()),
            eq("MESSAGE_PUBLISH"),
            eq("RATE_LIMIT_EXCEEDED"),
            anyString()
        );
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }

    @Test
    public void testClearCacheForCaseCreate_Success() {
        // Given
        Long caseId = 1L;
        String operator = CacheTestData.createTestOperator();

        when(cacheSecurityGuard.checkPermission(operator, "CASE_CREATE")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(operator, "CASE_CREATE")).thenReturn(true);

        // When
        caseCacheService.clearCacheForCaseCreate(caseId, operator);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(operator, "CASE_CREATE");
        verify(cacheSecurityGuard, times(1)).checkRateLimit(operator, "CASE_CREATE");
        verify(redisService, times(2)).del(anyString()); // 两个缓存清理操作
    }

    @Test
    public void testClearCacheForCaseUpdate() {
        // Given
        Long caseId = 1L;
        String operator = CacheTestData.createTestOperator();

        when(cacheSecurityGuard.checkPermission(operator, "CASE_UPDATE")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(operator, "CASE_UPDATE")).thenReturn(true);

        // When
        caseCacheService.clearCacheForCaseUpdate(caseId, operator);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(operator, "CASE_UPDATE");
        verify(cacheSecurityGuard, times(1)).checkRateLimit(operator, "CASE_UPDATE");
        verify(redisService, times(4)).del(anyString()); // 四个缓存清理操作
    }

    @Test
    public void testClearCacheForCaseDelete() {
        // Given
        Long caseId = 1L;
        String operator = CacheTestData.createTestOperator();

        when(cacheSecurityGuard.checkPermission(operator, "CASE_DELETE")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(operator, "CASE_DELETE")).thenReturn(true);

        // When
        caseCacheService.clearCacheForCaseDelete(caseId, operator);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(operator, "CASE_DELETE");
        verify(cacheSecurityGuard, times(1)).checkRateLimit(operator, "CASE_DELETE");
        verify(redisService, times(5)).del(anyString()); // 五个缓存清理操作
    }

    @Test
    public void testClearCacheForCaseBatchDelete() {
        // Given
        List<Long> caseIds = CacheTestData.createTestCaseIds();
        String operator = CacheTestData.createTestOperator();

        when(cacheSecurityGuard.checkPermission(operator, "CASE_BATCH_DELETE")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(operator, "CASE_BATCH_DELETE")).thenReturn(true);

        // When
        caseCacheService.clearCacheForCaseBatchDelete(caseIds, operator);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(operator, "CASE_BATCH_DELETE");
        verify(cacheSecurityGuard, times(1)).checkRateLimit(operator, "CASE_BATCH_DELETE");
        // 验证调用次数：每个案例ID一次 + 四个列表缓存
        verify(redisService, times(caseIds.size() + 4)).del(anyString());
    }

    @Test
    public void testClearCacheForCaseStatusUpdate() {
        // Given
        Long caseId = 1L;
        String operator = CacheTestData.createTestOperator();

        when(cacheSecurityGuard.checkPermission(operator, "CASE_STATUS_UPDATE")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(operator, "CASE_STATUS_UPDATE")).thenReturn(true);

        // When
        caseCacheService.clearCacheForCaseStatusUpdate(caseId, operator);

        // Then
        verify(cacheSecurityGuard, times(1)).checkPermission(operator, "CASE_STATUS_UPDATE");
        verify(cacheSecurityGuard, times(1)).checkRateLimit(operator, "CASE_STATUS_UPDATE");
        verify(redisService, times(4)).del(anyString()); // 四个缓存清理操作
    }

    @Test
    public void testDelHotCaseList() {
        // Given
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:hot";

        // When
        caseCacheService.delHotCaseList();

        // Then
        verify(redisService, times(1)).del(expectedKey);
    }

    @Test
    public void testDelLatestCaseList() {
        // Given
        String expectedKey = REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data:latest";

        // When
        caseCacheService.delLatestCaseList();

        // Then
        verify(redisService, times(1)).del(expectedKey);
    }

    @Test
    public void testDelCaseDataList() {
        // When
        caseCacheService.delCaseDataList();

        // Then
        verify(redisService, times(2)).del(anyString()); // hot 和 latest 缓存
    }

    @Test
    public void testPublishCacheUpdateMessage_JsonSerializationError() throws Exception {
        // Given
        CacheUpdateMessage message = CacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            "test_user"
        );

        when(cacheSecurityGuard.checkPermission(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(true);
        when(cacheSecurityGuard.checkRateLimit(message.getOperator(), "MESSAGE_PUBLISH")).thenReturn(true);
        when(objectMapper.writeValueAsString(message)).thenThrow(new RuntimeException("JSON序列化失败"));

        // When
        caseCacheService.publishCacheUpdateMessage(message);

        // Then
        verify(cacheSecurityGuard, times(1)).logSecurityEvent(
            eq(message.getOperator()),
            eq("MESSAGE_PUBLISH"),
            eq("OPERATION_FAILED"),
            contains("消息发布失败")
        );
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }
}
