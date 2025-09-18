package com.macro.mall.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.constant.CacheKeyConstants;
import com.macro.mall.dto.CacheUpdateMessage;
import com.macro.mall.service.CaseCacheService;
import com.macro.mall.test.util.CacheTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缓存同步集成测试
 * 测试管理端到门户端的完整缓存同步流程
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class CacheSyncIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:6-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private CaseCacheService caseCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.redis.database", () -> "15");
        // 确保测试环境使用与开发环境一致的配置模式
    }

    @BeforeEach
    public void setUp() {
        // 清理Redis测试数据
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }

    @Test
    public void testCacheCreateMessagePublishAndReceive() throws Exception {
        // Given
        Long caseId = 1L;
        String operator = CacheTestData.createTestOperator();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CacheUpdateMessage> receivedMessage = new AtomicReference<>();

        // 设置消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, pattern) -> {
            try {
                String messageBody = new String(message.getBody());
                CacheUpdateMessage cacheMessage = objectMapper.readValue(messageBody, CacheUpdateMessage.class);
                receivedMessage.set(cacheMessage);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL));

        container.start();

        try {
            // When - 执行缓存清理操作，应该发布消息
            caseCacheService.clearCacheForCaseCreate(caseId, operator);

            // Then - 验证消息被接收
            assertTrue(latch.await(5, TimeUnit.SECONDS), "应该在5秒内接收到消息");

            CacheUpdateMessage message = receivedMessage.get();
            assertNotNull(message, "应该接收到缓存更新消息");
            assertEquals(CacheUpdateMessage.ActionType.CREATE, message.getAction());
            assertEquals(CacheUpdateMessage.ResourceType.CASE, message.getResourceType());
            assertEquals(caseId.toString(), message.getResourceId());
            assertEquals(operator, message.getOperator());
            assertNotNull(message.getTimestamp());
            assertNotNull(message.getCacheKeys());
            assertFalse(message.getCacheKeys().isEmpty());

        } finally {
            container.stop();
        }
    }

    @Test
    public void testCacheUpdateMessagePublishAndReceive() throws Exception {
        // Given
        Long caseId = 2L;
        String operator = CacheTestData.createTestOperator();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CacheUpdateMessage> receivedMessage = new AtomicReference<>();

        // 设置消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, pattern) -> {
            try {
                String messageBody = new String(message.getBody());
                CacheUpdateMessage cacheMessage = objectMapper.readValue(messageBody, CacheUpdateMessage.class);
                receivedMessage.set(cacheMessage);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL));

        container.start();

        try {
            // When
            caseCacheService.clearCacheForCaseUpdate(caseId, operator);

            // Then
            assertTrue(latch.await(5, TimeUnit.SECONDS));

            CacheUpdateMessage message = receivedMessage.get();
            assertNotNull(message);
            assertEquals(CacheUpdateMessage.ActionType.UPDATE, message.getAction());
            assertEquals(caseId.toString(), message.getResourceId());

        } finally {
            container.stop();
        }
    }

    @Test
    public void testCacheDeleteMessagePublishAndReceive() throws Exception {
        // Given
        Long caseId = 3L;
        String operator = CacheTestData.createTestOperator();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CacheUpdateMessage> receivedMessage = new AtomicReference<>();

        // 设置消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, pattern) -> {
            try {
                String messageBody = new String(message.getBody());
                CacheUpdateMessage cacheMessage = objectMapper.readValue(messageBody, CacheUpdateMessage.class);
                receivedMessage.set(cacheMessage);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL));

        container.start();

        try {
            // When
            caseCacheService.clearCacheForCaseDelete(caseId, operator);

            // Then
            assertTrue(latch.await(5, TimeUnit.SECONDS));

            CacheUpdateMessage message = receivedMessage.get();
            assertNotNull(message);
            assertEquals(CacheUpdateMessage.ActionType.DELETE, message.getAction());
            assertEquals(caseId.toString(), message.getResourceId());

        } finally {
            container.stop();
        }
    }

    @Test
    public void testCacheBatchDeleteMessagePublishAndReceive() throws Exception {
        // Given
        List<Long> caseIds = java.util.Arrays.asList(1L, 2L, 3L);
        String operator = "test_admin";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<CacheUpdateMessage> receivedMessage = new AtomicReference<>();

        // 设置消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, pattern) -> {
            try {
                String messageBody = new String(message.getBody());
                CacheUpdateMessage cacheMessage = objectMapper.readValue(messageBody, CacheUpdateMessage.class);
                receivedMessage.set(cacheMessage);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL));

        container.start();

        try {
            // When
            caseCacheService.clearCacheForCaseBatchDelete(caseIds, operator);

            // Then
            assertTrue(latch.await(5, TimeUnit.SECONDS));

            CacheUpdateMessage message = receivedMessage.get();
            assertNotNull(message);
            assertEquals(CacheUpdateMessage.ActionType.BATCH_DELETE, message.getAction());
            assertEquals(caseIds.toString(), message.getResourceId());

        } finally {
            container.stop();
        }
    }

    @Test
    public void testMessageSerializationAndDeserialization() throws Exception {
        // Given
        CacheUpdateMessage originalMessage = CacheTestData.createCacheUpdateMessage(
            CacheUpdateMessage.ActionType.CREATE,
            CacheUpdateMessage.ResourceType.CASE,
            "123",
            "test_user"
        );

        // When - 序列化
        String json = objectMapper.writeValueAsString(originalMessage);
        assertNotNull(json);
        assertFalse(json.isEmpty());

        // Then - 反序列化
        CacheUpdateMessage deserializedMessage = objectMapper.readValue(json, CacheUpdateMessage.class);
        assertNotNull(deserializedMessage);
        assertEquals(originalMessage.getAction(), deserializedMessage.getAction());
        assertEquals(originalMessage.getResourceType(), deserializedMessage.getResourceType());
        assertEquals(originalMessage.getResourceId(), deserializedMessage.getResourceId());
        assertEquals(originalMessage.getOperator(), deserializedMessage.getOperator());
        assertEquals(originalMessage.getTimestamp(), deserializedMessage.getTimestamp());
    }

    @Test
    public void testRedisConnectionAndBasicOperations() {
        // Given
        String testKey = "test:cache:key";
        String testValue = "test_value";

        // When
        redisTemplate.opsForValue().set(testKey, testValue);
        String retrievedValue = (String) redisTemplate.opsForValue().get(testKey);

        // Then
        assertEquals(testValue, retrievedValue);

        // Cleanup
        redisTemplate.delete(testKey);
        assertNull(redisTemplate.opsForValue().get(testKey));
    }

    @Test
    public void testMultipleMessagePublishAndReceive() throws Exception {
        // Given
        int messageCount = 5;
        CountDownLatch latch = new CountDownLatch(messageCount);
        String operator = CacheTestData.createTestOperator();

        // 设置消息监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((message, pattern) -> {
            latch.countDown();
        }, new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL));

        container.start();

        try {
            // When - 发布多个消息
            for (int i = 1; i <= messageCount; i++) {
                caseCacheService.clearCacheForCaseCreate((long) i, operator);
            }

            // Then - 验证所有消息都被接收
            assertTrue(latch.await(10, TimeUnit.SECONDS),
                "应该在10秒内接收到所有" + messageCount + "条消息");

        } finally {
            container.stop();
        }
    }

    @Test
    public void testMessageChannelIsolation() throws Exception {
        // Given
        String testChannel = "test:different:channel";
        String operator = CacheTestData.createTestOperator();
        CountDownLatch correctChannelLatch = new CountDownLatch(1);
        CountDownLatch wrongChannelLatch = new CountDownLatch(1);

        // 设置正确频道的监听器
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);

        container.addMessageListener((message, pattern) -> {
            correctChannelLatch.countDown();
        }, new ChannelTopic(CacheKeyConstants.CACHE_UPDATE_CHANNEL));

        // 设置错误频道的监听器
        container.addMessageListener((message, pattern) -> {
            wrongChannelLatch.countDown();
        }, new ChannelTopic(testChannel));

        container.start();

        try {
            // When - 发送消息到正确的频道
            caseCacheService.clearCacheForCaseCreate(1L, operator);

            // Then - 只有正确频道的监听器应该接收到消息
            assertTrue(correctChannelLatch.await(5, TimeUnit.SECONDS),
                "正确频道应该接收到消息");
            assertFalse(wrongChannelLatch.await(2, TimeUnit.SECONDS),
                "错误频道不应该接收到消息");

        } finally {
            container.stop();
        }
    }
}
