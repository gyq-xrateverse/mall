package com.macro.mall.component;

import com.macro.mall.common.service.RedisService;
import com.macro.mall.service.CaseCacheService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 缓存故障恢复机制测试
 * 测试Redis故障检测、降级策略和自动恢复功能
 */
@ExtendWith(MockitoExtension.class)
class CacheFailureRecoveryTest {

    @Mock
    private RedisService redisService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private CaseCacheService caseCacheService;

    @Mock
    private CacheSecurityGuard cacheSecurityGuard;

    @InjectMocks
    private CacheFailureRecovery cacheFailureRecovery;

    @BeforeEach
    public void setUp() {
        // Mock RedisTemplate操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 设置初始状态
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", true);
        ReflectionTestUtils.setField(cacheFailureRecovery, "lastHealthCheckTime", System.currentTimeMillis());
    }

    @AfterEach
    public void tearDown() {
        // 确保测试后清理资源
        try {
            cacheFailureRecovery.shutdown();
        } catch (Exception e) {
            // 忽略关闭异常
        }
    }

    @Test
    public void testCheckRedisHealth_Healthy_ShouldMaintainAvailableStatus() throws Exception {
        // Given
        doNothing().when(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));
        when(valueOperations.get(anyString())).thenReturn("ok");

        // When
        invokePrivateMethod("checkRedisHealth");

        // Then
        assertTrue(cacheFailureRecovery.isRedisAvailable());
        verify(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));
        verify(valueOperations).get(anyString());
    }

    @Test
    public void testCheckRedisHealth_Unhealthy_ShouldMarkAsUnavailable() throws Exception {
        // Given
        doNothing().when(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));
        when(valueOperations.get(anyString())).thenReturn("not ok");

        // When
        invokePrivateMethod("checkRedisHealth");

        // Then
        assertFalse(cacheFailureRecovery.isRedisAvailable());
        verify(cacheSecurityGuard).logSecurityEvent("system", "REDIS_FAILURE", "SYSTEM_EVENT",
            "Redis连接失败，启动降级模式");
    }

    @Test
    public void testCheckRedisHealth_Exception_ShouldMarkAsUnavailable() throws Exception {
        // Given
        doThrow(new RuntimeException("Redis连接失败")).when(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));

        // When
        invokePrivateMethod("checkRedisHealth");

        // Then
        assertFalse(cacheFailureRecovery.isRedisAvailable());
        verify(cacheSecurityGuard).logSecurityEvent("system", "REDIS_FAILURE", "SYSTEM_EVENT",
            "Redis连接失败，启动降级模式");
    }

    @Test
    public void testCheckRedisHealth_StatusChange_FromHealthyToUnhealthy() throws Exception {
        // Given - 初始状态为健康
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", true);
        doNothing().when(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));
        when(valueOperations.get(anyString())).thenReturn(null); // 模拟连接失败

        // When
        invokePrivateMethod("checkRedisHealth");

        // Then
        assertFalse(cacheFailureRecovery.isRedisAvailable());
        verify(cacheSecurityGuard).logSecurityEvent(eq("system"), eq("REDIS_FAILURE"), eq("SYSTEM_EVENT"),
            eq("Redis连接失败，启动降级模式"));
    }

    @Test
    public void testCheckRedisHealth_StatusChange_FromUnhealthyToHealthy() throws Exception {
        // Given - 初始状态为不健康
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", false);
        doNothing().when(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));
        when(valueOperations.get(anyString())).thenReturn("ok");

        // When
        invokePrivateMethod("checkRedisHealth");

        // Then
        assertTrue(cacheFailureRecovery.isRedisAvailable());
        verify(cacheSecurityGuard).logSecurityEvent(eq("system"), eq("REDIS_RECOVERY"), eq("SYSTEM_EVENT"),
            eq("Redis连接恢复正常"));
    }

    @Test
    public void testAttemptRecovery_Success_ShouldMarkAsAvailable() throws Exception {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", false);
        doNothing().when(valueOperations).set("recovery:test", "test", 5L, TimeUnit.SECONDS);

        // When
        cacheFailureRecovery.attemptRecovery();

        // Then
        assertTrue(cacheFailureRecovery.isRedisAvailable());
        verify(valueOperations).set("recovery:test", "test", 5L, TimeUnit.SECONDS);
        verify(cacheSecurityGuard).logSecurityEvent(eq("system"), eq("REDIS_RECOVERY"), eq("SYSTEM_EVENT"),
            eq("Redis连接恢复正常"));
    }

    @Test
    public void testAttemptRecovery_Failed_ShouldThrowException() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", false);
        doThrow(new RuntimeException("连接失败")).when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

        // When & Then
        assertThrows(RuntimeException.class, () -> cacheFailureRecovery.attemptRecovery());
        assertFalse(cacheFailureRecovery.isRedisAvailable());
    }

    @Test
    public void testSafeCacheOperation_RedisAvailable_ShouldExecuteOperation() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", true);
        Runnable mockOperation = mock(Runnable.class);

        // When
        boolean result = cacheFailureRecovery.safeCacheOperation(mockOperation, "测试操作");

        // Then
        assertTrue(result);
        verify(mockOperation).run();
    }

    @Test
    public void testSafeCacheOperation_RedisUnavailable_ShouldSkipOperation() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", false);
        Runnable mockOperation = mock(Runnable.class);

        // When
        boolean result = cacheFailureRecovery.safeCacheOperation(mockOperation, "测试操作");

        // Then
        assertFalse(result);
        verify(mockOperation, never()).run();
    }

    @Test
    public void testSafeCacheOperation_OperationException_ShouldReturnFalse() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", true);
        Runnable failingOperation = () -> {
            throw new RuntimeException("操作失败");
        };

        // When
        boolean result = cacheFailureRecovery.safeCacheOperation(failingOperation, "失败操作");

        // Then
        assertFalse(result);
        assertTrue(cacheFailureRecovery.isRedisAvailable()); // 非连接错误不应影响状态
    }

    @Test
    public void testSafeCacheOperation_ConnectionException_ShouldMarkUnavailable() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", true);
        Runnable connectionFailingOperation = () -> {
            throw new RuntimeException("Connection failed");
        };

        // When
        boolean result = cacheFailureRecovery.safeCacheOperation(connectionFailingOperation, "连接失败操作");

        // Then
        assertFalse(result);
        assertFalse(cacheFailureRecovery.isRedisAvailable());
        verify(cacheSecurityGuard).logSecurityEvent(eq("system"), eq("REDIS_FAILURE"), eq("SYSTEM_EVENT"),
            eq("Redis连接失败，启动降级模式"));
    }

    @Test
    public void testSafeMessagePublish_Success_ShouldPublishMessage() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", true);
        String channel = "test:channel";
        String message = "test message";
        doNothing().when(redisTemplate).convertAndSend(channel, message);

        // When
        boolean result = cacheFailureRecovery.safeMessagePublish(channel, message);

        // Then
        assertTrue(result);
        verify(redisTemplate).convertAndSend(channel, message);
    }

    @Test
    public void testSafeMessagePublish_RedisUnavailable_ShouldSkip() {
        // Given
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", false);
        String channel = "test:channel";
        String message = "test message";

        // When
        boolean result = cacheFailureRecovery.safeMessagePublish(channel, message);

        // Then
        assertFalse(result);
        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }

    @Test
    public void testEmergencyCacheClear_Success_ShouldClearCacheAndLog() {
        // Given
        String operator = "admin";
        doNothing().when(caseCacheService).batchClearCaseListCache(operator);

        // When
        cacheFailureRecovery.emergencyCacheClear(operator);

        // Then
        verify(caseCacheService).batchClearCaseListCache(operator);
        verify(cacheSecurityGuard).logSecurityEvent(operator, "EMERGENCY_CLEAR", "ADMIN_OPERATION",
            "执行紧急缓存清理");
    }

    @Test
    public void testEmergencyCacheClear_Exception_ShouldNotThrow() {
        // Given
        String operator = "admin";
        doThrow(new RuntimeException("清理失败")).when(caseCacheService).batchClearCaseListCache(operator);

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> cacheFailureRecovery.emergencyCacheClear(operator));
    }

    @Test
    public void testIsConnectionError_WithConnectionErrors_ShouldReturnTrue() {
        // When & Then
        assertTrue(invokePrivateIsConnectionError(new RuntimeException("Connection refused")));
        assertTrue(invokePrivateIsConnectionError(new RuntimeException("connection timeout")));
        assertTrue(invokePrivateIsConnectionError(new RuntimeException("Unable to connect to Redis")));
        assertTrue(invokePrivateIsConnectionError(new RuntimeException("timeout occurred")));
    }

    @Test
    public void testIsConnectionError_WithNonConnectionErrors_ShouldReturnFalse() {
        // When & Then
        assertFalse(invokePrivateIsConnectionError(new RuntimeException("Invalid command")));
        assertFalse(invokePrivateIsConnectionError(new RuntimeException("Authentication failed")));
        assertFalse(invokePrivateIsConnectionError(new RuntimeException("Data format error")));
        assertFalse(invokePrivateIsConnectionError(new RuntimeException("")));
    }

    @Test
    public void testIsConnectionError_WithNullMessage_ShouldReturnFalse() {
        // Given
        RuntimeException exceptionWithNullMessage = new RuntimeException() {
            @Override
            public String getMessage() {
                return null;
            }
        };

        // When & Then
        assertFalse(invokePrivateIsConnectionError(exceptionWithNullMessage));
    }

    @Test
    public void testGettersAndSetters() {
        // Given
        long testTime = System.currentTimeMillis() - 1000;
        ReflectionTestUtils.setField(cacheFailureRecovery, "redisAvailable", false);
        ReflectionTestUtils.setField(cacheFailureRecovery, "lastHealthCheckTime", testTime);

        // When & Then
        assertFalse(cacheFailureRecovery.isRedisAvailable());
        assertEquals(testTime, cacheFailureRecovery.getLastHealthCheckTime());
    }

    @Test
    public void testShutdown_ShouldGracefullyStopScheduler() throws Exception {
        // When
        assertDoesNotThrow(() -> cacheFailureRecovery.shutdown());

        // 验证shutdown方法被调用后不会再有定时任务执行
        // 这个测试主要验证方法不抛异常
    }

    @Test
    public void testOnRedisFailure_WithSecurityGuardException_ShouldNotThrow() throws Exception {
        // Given
        doThrow(new RuntimeException("安全组件异常")).when(cacheSecurityGuard)
            .logSecurityEvent(anyString(), anyString(), anyString(), anyString());

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> invokePrivateMethod("onRedisFailure"));
    }

    @Test
    public void testOnRedisRecovered_WithSecurityGuardException_ShouldNotThrow() throws Exception {
        // Given
        doThrow(new RuntimeException("安全组件异常")).when(cacheSecurityGuard)
            .logSecurityEvent(anyString(), anyString(), anyString(), anyString());

        // When & Then - 不应该抛出异常
        assertDoesNotThrow(() -> invokePrivateMethod("onRedisRecovered"));
    }

    @Test
    public void testHealthCheckUpdateTime() throws Exception {
        // Given
        long beforeTime = System.currentTimeMillis();
        doNothing().when(valueOperations).set(anyString(), eq("ok"), eq(10L), eq(TimeUnit.SECONDS));
        when(valueOperations.get(anyString())).thenReturn("ok");

        // When
        invokePrivateMethod("checkRedisHealth");

        // Then
        long afterTime = System.currentTimeMillis();
        long lastCheckTime = cacheFailureRecovery.getLastHealthCheckTime();
        assertTrue(lastCheckTime >= beforeTime && lastCheckTime <= afterTime);
    }

    // =================== 测试辅助方法 ===================

    /**
     * 调用私有方法的辅助方法
     */
    private void invokePrivateMethod(String methodName) throws Exception {
        java.lang.reflect.Method method = CacheFailureRecovery.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(cacheFailureRecovery);
    }

    /**
     * 调用私有isConnectionError方法的辅助方法
     */
    private boolean invokePrivateIsConnectionError(Exception e) {
        try {
            java.lang.reflect.Method method = CacheFailureRecovery.class.getDeclaredMethod("isConnectionError", Exception.class);
            method.setAccessible(true);
            return (Boolean) method.invoke(cacheFailureRecovery, e);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to invoke isConnectionError", ex);
        }
    }
}