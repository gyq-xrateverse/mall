package com.macro.mall.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.dto.CacheMonitorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

/**
 * 缓存监控控制器测试
 * 测试缓存健康检查、统计信息获取、缓存清理和测试消息发布功能
 */
@WebMvcTest(CacheMonitorController.class)
@ActiveProfiles("test")
class CacheMonitorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisService redisService;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private ValueOperations<String, Object> valueOperations;

    @Autowired
    private ObjectMapper objectMapper;

    private CacheMonitorController cacheMonitorController;

    private static final String REDIS_DATABASE = "test_mall";
    private static final String REDIS_KEY_CASE = "test_case";

    @BeforeEach
    public void setUp() {
        cacheMonitorController = new CacheMonitorController();
        ReflectionTestUtils.setField(cacheMonitorController, "redisService", redisService);
        ReflectionTestUtils.setField(cacheMonitorController, "redisTemplate", redisTemplate);
        ReflectionTestUtils.setField(cacheMonitorController, "REDIS_DATABASE", REDIS_DATABASE);
        ReflectionTestUtils.setField(cacheMonitorController, "REDIS_KEY_CASE", REDIS_KEY_CASE);

        // Mock RedisTemplate操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testGetCacheHealth_RedisHealthy_ShouldReturnCompleteHealthInfo() throws Exception {
        // Given
        // Mock Redis连接健康检查
        doNothing().when(valueOperations).set("health:check", "ok", 10);
        when(valueOperations.get("health:check")).thenReturn("ok");

        // Mock 缓存键统计
        Set<String> categoryKeys = createMockKeys("category", 5);
        Set<String> dataKeys = createMockKeys("data", 10);
        Set<String> hotKeys = createMockKeys("hot", 3);
        Set<String> latestKeys = createMockKeys("latest", 8);

        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category*")).thenReturn(categoryKeys);
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data*")).thenReturn(dataKeys);
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":hot*")).thenReturn(hotKeys);
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":latest*")).thenReturn(latestKeys);

        // When & Then
        mockMvc.perform(get("/admin/cache/monitor/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.redisConnected").value(true))
                .andExpect(jsonPath("$.data.status").value("healthy"))
                .andExpect(jsonPath("$.data.cacheStatistics").exists())
                .andExpect(jsonPath("$.data.memoryInfo").exists())
                .andExpect(jsonPath("$.data.messageStatistics").exists())
                .andExpect(jsonPath("$.data.timestamp").exists());

        // 验证Redis健康检查被调用
        verify(valueOperations).set("health:check", "ok", 10);
        verify(valueOperations).get("health:check");
    }

    @Test
    public void testGetCacheHealth_RedisUnhealthy_ShouldReturnUnhealthyStatus() throws Exception {
        // Given
        doThrow(new RuntimeException("Redis连接失败")).when(valueOperations).set(anyString(), anyString(), anyLong());

        // When & Then
        mockMvc.perform(get("/admin/cache/monitor/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.redisConnected").value(false))
                .andExpect(jsonPath("$.data.status").value("unhealthy"))
                .andExpect(jsonPath("$.data.cacheStatistics").doesNotExist())
                .andExpect(jsonPath("$.data.memoryInfo").doesNotExist())
                .andExpect(jsonPath("$.data.messageStatistics").doesNotExist());
    }

    @Test
    public void testGetCacheHealth_ExceptionThrown_ShouldReturnErrorResponse() throws Exception {
        // Given
        when(redisTemplate.opsForValue()).thenThrow(new RuntimeException("Redis服务异常"));

        // When & Then
        mockMvc.perform(get("/admin/cache/monitor/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("获取缓存健康状态失败")));
    }

    @Test
    public void testGetCacheStatistics_Success_ShouldReturnDetailedStats() throws Exception {
        // Given
        // Mock 管理端缓存统计
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":category*"))
            .thenReturn(createMockKeys("category", 3));
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":data*"))
            .thenReturn(createMockKeys("data", 15));
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":hot*"))
            .thenReturn(createMockKeys("hot", 5));
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":latest*"))
            .thenReturn(createMockKeys("latest", 10));

        // Mock 门户端缓存统计
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:category*"))
            .thenReturn(createMockKeys("portal:category", 2));
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:detail*"))
            .thenReturn(createMockKeys("portal:detail", 20));
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:hot*"))
            .thenReturn(createMockKeys("portal:hot", 3));
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":portal:latest*"))
            .thenReturn(createMockKeys("portal:latest", 8));

        // Mock 频率限制统计
        when(redisTemplate.keys(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":rate_limit*"))
            .thenReturn(createMockKeys("rate_limit", 5));

        // When & Then
        mockMvc.perform(get("/admin/cache/monitor/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.adminCacheStats").exists())
                .andExpect(jsonPath("$.data.portalCacheStats").exists())
                .andExpect(jsonPath("$.data.rateLimitStats").exists())
                .andExpect(jsonPath("$.data.timestamp").exists());

        // 验证所有统计查询都被调用
        verify(redisTemplate, times(9)).keys(anyString());
    }

    @Test
    public void testGetCacheStatistics_ExceptionThrown_ShouldReturnErrorResponse() throws Exception {
        // Given
        when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("Redis查询异常"));

        // When & Then
        mockMvc.perform(get("/admin/cache/monitor/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("获取缓存统计信息失败")));
    }

    @Test
    public void testCleanupExpiredCache_Success_ShouldReturnCleanupResult() throws Exception {
        // Given
        Set<String> rateLimitKeys = createMockKeys("rate_limit", 10);
        when(redisTemplate.keys(REDIS_DATABASE + ":rate_limit:*")).thenReturn(rateLimitKeys);

        // Mock hasKey检查（模拟其中5个键已过期）
        when(redisTemplate.hasKey(anyString()))
            .thenReturn(true, true, true, true, true, false, false, false, false, false);

        // Mock 缓存统计（用于清理前统计）
        when(redisTemplate.keys(contains("category"))).thenReturn(createMockKeys("category", 3));
        when(redisTemplate.keys(contains("data"))).thenReturn(createMockKeys("data", 8));
        when(redisTemplate.keys(contains("hot"))).thenReturn(createMockKeys("hot", 2));
        when(redisTemplate.keys(contains("latest"))).thenReturn(createMockKeys("latest", 5));

        // When & Then
        mockMvc.perform(post("/admin/cache/monitor/cleanup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.beforeCleanup").exists())
                .andExpect(jsonPath("$.data.cleanedKeys").value(5))
                .andExpect(jsonPath("$.data.timestamp").exists());

        // 验证清理逻辑被执行
        verify(redisTemplate).keys(REDIS_DATABASE + ":rate_limit:*");
        verify(redisTemplate, times(10)).hasKey(anyString());
    }

    @Test
    public void testCleanupExpiredCache_NoKeysToClean_ShouldReturnZeroCleanedCount() throws Exception {
        // Given
        when(redisTemplate.keys(REDIS_DATABASE + ":rate_limit:*")).thenReturn(Collections.emptySet());

        // Mock 缓存统计
        when(redisTemplate.keys(anyString())).thenReturn(Collections.emptySet());

        // When & Then
        mockMvc.perform(post("/admin/cache/monitor/cleanup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.cleanedKeys").value(0));
    }

    @Test
    public void testCleanupExpiredCache_ExceptionThrown_ShouldReturnErrorResponse() throws Exception {
        // Given
        when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("Redis异常"));

        // When & Then
        mockMvc.perform(post("/admin/cache/monitor/cleanup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("清理过期缓存失败")));
    }

    @Test
    public void testTestMessagePublish_Success_ShouldPublishTestMessage() throws Exception {
        // Given
        String testMessage = "这是一条测试消息";
        doNothing().when(redisTemplate).convertAndSend(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/admin/cache/monitor/test/message")
                .param("testMessage", testMessage))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("测试消息发布成功"));

        // 验证消息发布被调用
        verify(redisTemplate).convertAndSend("test:cache:update", testMessage);
    }

    @Test
    public void testTestMessagePublish_EmptyMessage_ShouldStillPublish() throws Exception {
        // Given
        String emptyMessage = "";
        doNothing().when(redisTemplate).convertAndSend(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/admin/cache/monitor/test/message")
                .param("testMessage", emptyMessage))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(redisTemplate).convertAndSend("test:cache:update", emptyMessage);
    }

    @Test
    public void testTestMessagePublish_ExceptionThrown_ShouldReturnErrorResponse() throws Exception {
        // Given
        String testMessage = "测试消息";
        doThrow(new RuntimeException("消息发布异常")).when(redisTemplate).convertAndSend(anyString(), anyString());

        // When & Then
        mockMvc.perform(post("/admin/cache/monitor/test/message")
                .param("testMessage", testMessage))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("测试消息发布失败")));
    }

    @Test
    public void testCacheStatistics_EmptyCache_ShouldReturnZeroCountsGracefully() throws Exception {
        // Given - 所有缓存查询返回空集合
        when(redisTemplate.keys(anyString())).thenReturn(Collections.emptySet());

        // When & Then
        mockMvc.perform(get("/admin/cache/monitor/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.adminCacheStats").exists())
                .andExpect(jsonPath("$.data.portalCacheStats").exists())
                .andExpect(jsonPath("$.data.rateLimitStats").exists());
    }

    @Test
    public void testHealthCheck_PartialRedisFailure_ShouldHandleGracefully() throws Exception {
        // Given - Redis连接检查成功，但统计查询失败
        doNothing().when(valueOperations).set("health:check", "ok", 10);
        when(valueOperations.get("health:check")).thenReturn("ok");
        when(redisTemplate.keys(anyString())).thenThrow(new RuntimeException("统计查询失败"));

        // When & Then - 应该返回健康状态但统计信息为空
        mockMvc.perform(get("/admin/cache/monitor/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.redisConnected").value(true))
                .andExpect(jsonPath("$.data.status").value("healthy"));
    }

    @Test
    public void testMethodsWithNullRedisTemplate_ShouldHandleGracefully() throws Exception {
        // Given
        CacheMonitorController nullRedisController = new CacheMonitorController();
        ReflectionTestUtils.setField(nullRedisController, "redisTemplate", null);

        // When & Then - 应该抛出异常并被正确处理
        mockMvc.perform(get("/admin/cache/monitor/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // =================== 测试辅助方法 ===================

    /**
     * 创建模拟的缓存键集合
     */
    private Set<String> createMockKeys(String pattern, int count) {
        Set<String> keys = new HashSet<>();
        for (int i = 1; i <= count; i++) {
            keys.add(REDIS_DATABASE + ":" + REDIS_KEY_CASE + ":" + pattern + ":" + i);
        }
        return keys;
    }
}