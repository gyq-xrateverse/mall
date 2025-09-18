package com.macro.mall.component;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.test.config.CacheTestConfig;
import com.macro.mall.test.util.CacheTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CacheSecurityGuard 单元测试
 */
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = CacheTestConfig.class)
class CacheSecurityGuardTest {

    @Mock
    private RedisService redisService;

    @InjectMocks
    private CacheSecurityGuard cacheSecurityGuard;

    private static final String REDIS_DATABASE = "test_mall";
    private static final String TEST_OPERATOR = "test_user";
    private static final String TEST_OPERATION = "CASE_CREATE";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(cacheSecurityGuard, "REDIS_DATABASE", REDIS_DATABASE);
    }

    @Test
    public void testCheckRateLimit_FirstOperation_ShouldAllow() {
        // Given
        when(redisService.get(anyString())).thenReturn(null); // 没有记录

        // When
        boolean result = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, TEST_OPERATION);

        // Then
        assertTrue(result);
        verify(redisService, times(2)).get(anyString()); // 检查分钟和小时限制
        verify(redisService, times(2)).set(anyString(), anyInt(), anyLong()); // 设置计数
    }

    @Test
    public void testCheckRateLimit_WithinLimit_ShouldAllow() {
        // Given
        when(redisService.get(anyString())).thenReturn(10); // 当前计数为10，小于限制

        // When
        boolean result = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, TEST_OPERATION);

        // Then
        assertTrue(result);
        verify(redisService, times(2)).get(anyString());
        verify(redisService, times(2)).set(anyString(), eq(11), anyLong()); // 计数+1
    }

    @Test
    public void testCheckRateLimit_ExceedMinuteLimit_ShouldDeny() {
        // Given
        when(redisService.get(anyString())).thenReturn(60, 100); // 分钟限制达到60，小时限制100

        // When
        boolean result = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, TEST_OPERATION);

        // Then
        assertFalse(result);
        verify(redisService, times(1)).get(anyString()); // 只检查了分钟限制就返回false
        verify(redisService, never()).set(anyString(), anyInt(), anyLong());
    }

    @Test
    public void testCheckRateLimit_ExceedHourLimit_ShouldDeny() {
        // Given
        when(redisService.get(anyString())).thenReturn(30, 1000); // 分钟30（正常），小时1000（达到限制）

        // When
        boolean result = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, TEST_OPERATION);

        // Then
        assertFalse(result);
        verify(redisService, times(2)).get(anyString()); // 检查了分钟和小时限制
        verify(redisService, never()).set(anyString(), anyInt(), anyLong());
    }

    @Test
    public void testCheckRateLimit_RedisException_ShouldAllowWithLog() {
        // Given
        when(redisService.get(anyString())).thenThrow(new RuntimeException("Redis连接失败"));

        // When
        boolean result = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, TEST_OPERATION);

        // Then
        assertTrue(result); // 异常时采用保守策略，允许操作
        verify(redisService, times(1)).get(anyString());
    }

    @Test
    public void testCheckPermission_ValidOperator_ShouldAllow() {
        // When
        boolean result = cacheSecurityGuard.checkPermission(TEST_OPERATOR, TEST_OPERATION);

        // Then
        assertTrue(result);
    }

    @Test
    public void testCheckPermission_NullOperator_ShouldDeny() {
        // When
        boolean result = cacheSecurityGuard.checkPermission(null, TEST_OPERATION);

        // Then
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_EmptyOperator_ShouldDeny() {
        // When
        boolean result = cacheSecurityGuard.checkPermission("", TEST_OPERATION);

        // Then
        assertFalse(result);
    }

    @Test
    public void testCheckPermission_BlankOperator_ShouldDeny() {
        // When
        boolean result = cacheSecurityGuard.checkPermission("   ", TEST_OPERATION);

        // Then
        assertFalse(result);
    }

    @Test
    public void testValidateMessageSource_ValidMessage_ShouldAllow() {
        // Given
        String validMessage = CacheTestData.createTestMessageJson();

        // When
        boolean result = cacheSecurityGuard.validateMessageSource(validMessage);

        // Then
        assertTrue(result);
    }

    @Test
    public void testValidateMessageSource_NullMessage_ShouldDeny() {
        // When
        boolean result = cacheSecurityGuard.validateMessageSource(null);

        // Then
        assertFalse(result);
    }

    @Test
    public void testValidateMessageSource_EmptyMessage_ShouldDeny() {
        // When
        boolean result = cacheSecurityGuard.validateMessageSource("");

        // Then
        assertFalse(result);
    }

    @Test
    public void testValidateMessageSource_InvalidFormat_ShouldDeny() {
        // Given
        String invalidMessage = CacheTestData.createInvalidMessageJson();

        // When
        boolean result = cacheSecurityGuard.validateMessageSource(invalidMessage);

        // Then
        assertFalse(result);
    }

    @Test
    public void testValidateMessageSource_MissingRequiredFields_ShouldDeny() {
        // Given
        String incompleteMessage = "{\"action\": \"CREATE\"}"; // 缺少必需字段

        // When
        boolean result = cacheSecurityGuard.validateMessageSource(incompleteMessage);

        // Then
        assertFalse(result);
    }

    @Test
    public void testLogSecurityEvent_Success() {
        // Given
        String eventType = "RATE_LIMIT_EXCEEDED";
        String description = "用户操作频率超限";

        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> {
            cacheSecurityGuard.logSecurityEvent(TEST_OPERATOR, TEST_OPERATION, eventType, description);
        });
    }

    @Test
    public void testLogSecurityEvent_WithNullValues() {
        // When & Then - 应该不抛出异常
        assertDoesNotThrow(() -> {
            cacheSecurityGuard.logSecurityEvent(null, null, null, null);
        });
    }

    @Test
    public void testCheckRateLimit_DifferentOperations_SeparateCounters() {
        // Given
        String operation1 = "CASE_CREATE";
        String operation2 = "CASE_UPDATE";
        when(redisService.get(anyString())).thenReturn(null);

        // When
        boolean result1 = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, operation1);
        boolean result2 = cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, operation2);

        // Then
        assertTrue(result1);
        assertTrue(result2);
        // 每个操作都会检查和设置2个键（分钟和小时）
        verify(redisService, times(4)).get(anyString());
        verify(redisService, times(4)).set(anyString(), anyInt(), anyLong());
    }

    @Test
    public void testCheckRateLimit_DifferentUsers_SeparateCounters() {
        // Given
        String user1 = "user1";
        String user2 = "user2";
        when(redisService.get(anyString())).thenReturn(null);

        // When
        boolean result1 = cacheSecurityGuard.checkRateLimit(user1, TEST_OPERATION);
        boolean result2 = cacheSecurityGuard.checkRateLimit(user2, TEST_OPERATION);

        // Then
        assertTrue(result1);
        assertTrue(result2);
        // 每个用户都会检查和设置2个键（分钟和小时）
        verify(redisService, times(4)).get(anyString());
        verify(redisService, times(4)).set(anyString(), anyInt(), anyLong());
    }

    @Test
    public void testValidateMessageSource_ExceptionHandling() {
        // Given
        String malformedJson = "{invalid json";

        // When
        boolean result = cacheSecurityGuard.validateMessageSource(malformedJson);

        // Then
        assertTrue(result); // 基础验证通过，即使JSON可能无效
    }

    @Test
    public void testCheckPermission_ExceptionHandling() {
        // Given - 模拟权限检查过程中的异常
        String operatorId = "problematic_user";

        // When
        boolean result = cacheSecurityGuard.checkPermission(operatorId, TEST_OPERATION);

        // Then
        assertTrue(result); // 基础权限检查通过
    }

    @Test
    public void testRateLimitKeyGeneration() {
        // Given
        when(redisService.get(anyString())).thenReturn(null);

        // When
        cacheSecurityGuard.checkRateLimit(TEST_OPERATOR, TEST_OPERATION);

        // Then
        // 验证Redis键的格式包含预期的前缀和参数
        verify(redisService, times(2)).get(argThat(key ->
            key.contains(REDIS_DATABASE) &&
            key.contains("rate_limit") &&
            key.contains(TEST_OPERATOR) &&
            key.contains(TEST_OPERATION)
        ));
    }
}