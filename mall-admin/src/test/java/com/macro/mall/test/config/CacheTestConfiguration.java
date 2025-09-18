package com.macro.mall.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.component.CacheFailureRecovery;
import com.macro.mall.component.CacheSecurityGuard;
import com.macro.mall.service.CaseCacheService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 缓存测试环境配置
 * 提供测试所需的Mock对象、测试Redis配置和测试专用Bean
 */
@TestConfiguration
public class CacheTestConfiguration {

    // =================== Mock Redis 相关配置 ===================

    /**
     * Mock Redis模板
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> mockRedisTemplate() {
        RedisTemplate<String, Object> template = Mockito.mock(RedisTemplate.class);

        // 配置序列化器
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;
    }

    /**
     * Mock Redis服务
     */
    @Bean
    @Primary
    public RedisService mockRedisService() {
        return Mockito.mock(RedisService.class);
    }

    /**
     * Mock Redis连接工厂
     */
    @Bean
    @Primary
    public RedisConnectionFactory mockRedisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    /**
     * Mock Redis消息监听容器
     */
    @Bean
    @Primary
    public RedisMessageListenerContainer mockRedisMessageListenerContainer() {
        RedisMessageListenerContainer container = Mockito.mock(RedisMessageListenerContainer.class);

        // 配置启动和停止行为
        Mockito.doNothing().when(container).start();
        Mockito.doNothing().when(container).stop();

        return container;
    }

    // =================== Mock 缓存服务配置 ===================

    /**
     * Mock 案例缓存服务
     */
    @Bean
    @Primary
    public CaseCacheService mockCaseCacheService() {
        return Mockito.mock(CaseCacheService.class);
    }


    // =================== Mock 安全和故障恢复组件 ===================

    /**
     * Mock 缓存安全防护
     */
    @Bean
    @Primary
    public CacheSecurityGuard mockCacheSecurityGuard() {
        CacheSecurityGuard guard = Mockito.mock(CacheSecurityGuard.class);

        // 默认允许所有权限检查
        Mockito.when(guard.checkPermission(Mockito.anyString(), Mockito.anyString()))
               .thenReturn(true);

        // 默认通过频率限制检查
        Mockito.when(guard.checkRateLimit(Mockito.anyString(), Mockito.anyString()))
               .thenReturn(true);

        // 默认验证通过消息
        Mockito.when(guard.validateMessageSource(Mockito.anyString()))
               .thenReturn(true);

        return guard;
    }

    /**
     * Mock 缓存故障恢复
     */
    @Bean
    @Primary
    public CacheFailureRecovery mockCacheFailureRecovery() {
        CacheFailureRecovery recovery = Mockito.mock(CacheFailureRecovery.class);

        // 默认Redis可用
        Mockito.when(recovery.isRedisAvailable()).thenReturn(true);

        // 默认安全操作成功
        Mockito.when(recovery.safeCacheOperation(Mockito.any(Runnable.class), Mockito.anyString()))
               .thenReturn(true);

        // 默认安全消息发布成功
        Mockito.when(recovery.safeMessagePublish(Mockito.anyString(), Mockito.any()))
               .thenReturn(true);

        return recovery;
    }

    // =================== 测试工具配置 ===================

    /**
     * ObjectMapper for JSON序列化测试
     */
    @Bean
    public ObjectMapper testObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * 测试用CountDownLatch工厂
     */
    @Bean
    public TestLatchFactory testLatchFactory() {
        return new TestLatchFactory();
    }

    /**
     * 测试同步工具
     */
    @Bean
    public TestSynchronizer testSynchronizer() {
        return new TestSynchronizer();
    }

    // =================== 内部工具类 ===================

    /**
     * 测试用CountDownLatch工厂
     */
    public static class TestLatchFactory {

        /**
         * 创建指定计数的CountDownLatch
         */
        public CountDownLatch createLatch(int count) {
            return new CountDownLatch(count);
        }

        /**
         * 创建超时等待的CountDownLatch
         */
        public CountDownLatch createTimeoutLatch(int count, long timeout, TimeUnit unit) {
            CountDownLatch latch = new CountDownLatch(count);
            // 可以在这里添加超时处理逻辑
            return latch;
        }
    }

    /**
     * 测试同步工具
     */
    public static class TestSynchronizer {

        /**
         * 等待指定时间
         */
        public void waitFor(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /**
         * 等待条件满足
         */
        public boolean waitForCondition(java.util.function.BooleanSupplier condition, long timeoutMs) {
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < timeoutMs) {
                if (condition.getAsBoolean()) {
                    return true;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return false;
        }

        /**
         * 并发执行任务
         */
        public void executeConcurrently(Runnable task, int threadCount) {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                new Thread(() -> {
                    try {
                        startLatch.await();
                        task.run();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        finishLatch.countDown();
                    }
                }).start();
            }

            startLatch.countDown(); // 启动所有线程

            try {
                finishLatch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // =================== 测试环境配置 ===================

    /**
     * 测试Redis配置
     */
    public static class TestRedisConfig {

        public static final String TEST_DATABASE = "test_mall";
        public static final String TEST_KEY_PREFIX = "test_case";
        public static final String TEST_CHANNEL = "test:cache:update";
        public static final int TEST_TIMEOUT = 3000;
        public static final int TEST_EXPIRE_TIME = 60;

        /**
         * 获取测试缓存键
         */
        public static String getTestCacheKey(String type, Object... params) {
            StringBuilder key = new StringBuilder(TEST_DATABASE);
            key.append(":").append(TEST_KEY_PREFIX);
            key.append(":").append(type);
            for (Object param : params) {
                key.append(":").append(param);
            }
            return key.toString();
        }
    }

    /**
     * 测试安全配置
     */
    public static class TestSecurityConfig {

        public static final int MAX_REQUESTS_PER_MINUTE = 10;
        public static final int MAX_REQUESTS_PER_HOUR = 100;
        public static final String[] ALLOWED_OPERATIONS = {
            "CACHE_CLEAR", "MESSAGE_PUBLISH", "HEALTH_CHECK"
        };

        /**
         * 检查操作是否被允许
         */
        public static boolean isOperationAllowed(String operation) {
            for (String allowed : ALLOWED_OPERATIONS) {
                if (allowed.equals(operation)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 测试性能配置
     */
    public static class TestPerformanceConfig {

        public static final long MAX_RESPONSE_TIME_MS = 50;
        public static final long MAX_MESSAGE_PUBLISH_TIME_MS = 100;
        public static final int MAX_CONCURRENT_OPERATIONS = 20;
        public static final long MEMORY_LIMIT_MB = 50;

        /**
         * 验证响应时间是否在允许范围内
         */
        public static boolean isResponseTimeAcceptable(long responseTimeMs) {
            return responseTimeMs <= MAX_RESPONSE_TIME_MS;
        }

        /**
         * 验证内存使用是否在允许范围内
         */
        public static boolean isMemoryUsageAcceptable(long memoryUsageMB) {
            return memoryUsageMB <= MEMORY_LIMIT_MB;
        }
    }

    // =================== 测试清理工具 ===================

    /**
     * 测试清理工具
     */
    @Bean
    public TestCleanupUtil testCleanupUtil() {
        return new TestCleanupUtil();
    }

    /**
     * 测试清理工具类
     */
    public static class TestCleanupUtil {

        /**
         * 清理测试环境
         */
        public void cleanupTestEnvironment() {
            // 重置Mock对象
            Mockito.reset();

            // 清理系统属性
            System.clearProperty("spring.profiles.active");

            // 可以添加更多清理逻辑
        }

        /**
         * 清理Redis测试数据
         */
        public void cleanupRedisTestData(RedisTemplate<String, Object> redisTemplate) {
            // 在真实Redis环境中，清理测试数据
            if (redisTemplate != null) {
                try {
                    String pattern = TestRedisConfig.TEST_DATABASE + ":" + TestRedisConfig.TEST_KEY_PREFIX + "*";
                    // 清理测试键
                    redisTemplate.delete(redisTemplate.keys(pattern));
                } catch (Exception e) {
                    // 忽略清理错误
                }
            }
        }

        /**
         * 验证测试环境状态
         */
        public boolean validateTestEnvironment() {
            // 验证必要的配置是否正确
            return "test".equals(System.getProperty("spring.profiles.active", ""));
        }
    }
}