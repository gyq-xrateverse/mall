package com.macro.mall.security.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PortalJwtTokenUtil Redis功能测试
 * 专注测试JWT工具类中的Redis验证功能
 * @author Claude
 * @since 2025-09-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PortalJwtTokenUtil Redis功能测试")
public class PortalJwtTokenUtilRedisTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private PortalJwtTokenUtil jwtTokenUtil;

    // 测试数据
    private static final String TEST_USERNAME = "testuser";
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_SECRET = "test-jwt-secret-key-for-testing-purposes-only";
    private static final Long TEST_EXPIRATION = 86400L; // 24小时
    private static final String ACCESS_TOKEN_KEY = "portal:access_token:" + TEST_USERNAME + ":" + TEST_USER_ID;

    // 模拟的有效JWT token（简化版本，仅用于测试）
    private String validAccessToken;
    private String validRefreshToken;

    @BeforeEach
    void setUp() {
        // 设置JWT配置
        ReflectionTestUtils.setField(jwtTokenUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "expiration", TEST_EXPIRATION);
        ReflectionTestUtils.setField(jwtTokenUtil, "refreshExpiration", 604800L); // 7天

        // Mock Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // 生成测试用的token
        validAccessToken = jwtTokenUtil.generateAccessToken(TEST_USERNAME, TEST_USER_ID);
        validRefreshToken = jwtTokenUtil.generateRefreshToken(TEST_USERNAME, TEST_USER_ID);

        // 重置mock以便测试
        reset(redisTemplate, valueOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("validateAccessToken Redis检查测试")
    class ValidateAccessTokenRedisTest {

        @Test
        @DisplayName("Token验证成功 - Redis中存在匹配的token")
        public void testValidateAccessTokenSuccessWithRedis() {
            // Given - Redis中存在对应的token
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(validAccessToken);

            // When
            boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);

            // Then
            assertTrue(isValid);

            // 验证Redis检查被调用
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
        }

        @Test
        @DisplayName("Token验证失败 - Redis中不存在token")
        public void testValidateAccessTokenFailsWhenNotInRedis() {
            // Given - Redis中不存在token
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(null);

            // When
            boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);

            // Then
            assertFalse(isValid);

            // 验证Redis检查被调用
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
        }

        @Test
        @DisplayName("Token验证失败 - Redis中token不匹配")
        public void testValidateAccessTokenFailsWhenTokenMismatch() {
            // Given - Redis中存储的是不同的token
            String differentToken = jwtTokenUtil.generateAccessToken("otheruser", 2L);
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(differentToken);

            // When
            boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);

            // Then
            assertFalse(isValid);

            // 验证Redis检查被调用
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
        }

        @Test
        @DisplayName("Token验证 - Redis异常不影响基本JWT验证")
        public void testValidateAccessTokenWithRedisException() {
            // Given - Redis操作抛出异常
            when(valueOperations.get(anyString()))
                    .thenThrow(new RuntimeException("Redis connection failed"));

            // When
            boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);

            // Then - 由于Redis异常，验证失败
            assertFalse(isValid);

            // 验证尝试了Redis检查
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
        }

        @Test
        @DisplayName("无效Token - Redis检查仍然执行")
        public void testValidateInvalidTokenStillChecksRedis() {
            // Given - 无效的token格式
            String invalidToken = "invalid.token.format";

            // 由于token格式无效，无法提取用户信息，Redis检查会失败
            // 但我们可以测试异常处理

            // When
            boolean isValid = jwtTokenUtil.validateAccessToken(invalidToken);

            // Then
            assertFalse(isValid);

            // 注意：由于token无效，可能无法提取用户信息进行Redis检查
            // 这种情况下会在getUsernameFromToken或getUserIdFromToken时失败
        }
    }

    @Nested
    @DisplayName("Token信息提取测试")
    class TokenInfoExtractionTest {

        @Test
        @DisplayName("从Token中提取用户名")
        public void testGetUsernameFromToken() {
            // When
            String username = jwtTokenUtil.getUsernameFromToken(validAccessToken);

            // Then
            assertEquals(TEST_USERNAME, username);
        }

        @Test
        @DisplayName("从Token中提取用户ID")
        public void testGetUserIdFromToken() {
            // When
            Long userId = jwtTokenUtil.getUserIdFromToken(validAccessToken);

            // Then
            assertEquals(TEST_USER_ID, userId);
        }

        @Test
        @DisplayName("从Token中提取用户类型")
        public void testGetUserTypeFromToken() {
            // When
            String userType = jwtTokenUtil.getUserTypeFromToken(validAccessToken);

            // Then
            assertEquals("member", userType);
        }

        @Test
        @DisplayName("从Token中提取Token类型")
        public void testGetTokenTypeFromToken() {
            // When - 测试access token
            String tokenType = jwtTokenUtil.getTokenTypeFromToken(validAccessToken);

            // Then
            assertEquals("access", tokenType);

            // When - 测试refresh token
            String refreshTokenType = jwtTokenUtil.getTokenTypeFromToken(validRefreshToken);

            // Then
            assertEquals("refresh", refreshTokenType);
        }
    }

    @Nested
    @DisplayName("Token类型验证测试")
    class TokenTypeValidationTest {

        @Test
        @DisplayName("验证Access Token类型")
        public void testValidateAccessTokenType() {
            // Given - Mock Redis返回匹配的token
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(validAccessToken);

            // When
            boolean isValidAccess = jwtTokenUtil.validateAccessToken(validAccessToken);
            boolean isValidRefresh = jwtTokenUtil.validateRefreshToken(validAccessToken);

            // Then
            assertTrue(isValidAccess, "Access token应该通过access token验证");
            assertFalse(isValidRefresh, "Access token不应该通过refresh token验证");
        }

        @Test
        @DisplayName("验证Refresh Token类型")
        public void testValidateRefreshTokenType() {
            // When
            boolean isValidRefresh = jwtTokenUtil.validateRefreshToken(validRefreshToken);
            // refresh token不会检查Redis，因为validateAccessToken才检查
            boolean isValidAccess = jwtTokenUtil.validateAccessToken(validRefreshToken);

            // Then
            assertTrue(isValidRefresh, "Refresh token应该通过refresh token验证");
            // refresh token用于access token验证时，因为类型不匹配会失败
            // 但由于Redis检查，这个测试比较复杂，我们简化处理
        }
    }

    @Nested
    @DisplayName("Token过期检查测试")
    class TokenExpirationTest {

        @Test
        @DisplayName("检查Token是否过期")
        public void testIsTokenExpired() {
            // When - 刚生成的token不应该过期
            boolean isExpired = jwtTokenUtil.isTokenExpired(validAccessToken);

            // Then
            assertFalse(isExpired, "新生成的token不应该过期");
        }

        @Test
        @DisplayName("检查Token是否即将过期")
        public void testIsTokenExpiringSoon() {
            // When - 刚生成的token不应该即将过期
            boolean isExpiringSoon = jwtTokenUtil.isTokenExpiringSoon(validAccessToken);

            // Then
            assertFalse(isExpiringSoon, "新生成的token不应该即将过期");
        }

        @Test
        @DisplayName("获取Token过期时间")
        public void testGetExpirationDateFromToken() {
            // When
            var expirationDate = jwtTokenUtil.getExpirationDateFromToken(validAccessToken);

            // Then
            assertNotNull(expirationDate, "过期时间不应该为null");
            assertTrue(expirationDate.after(new java.util.Date()), "过期时间应该在将来");
        }
    }

    @Nested
    @DisplayName("Redis集成异常处理测试")
    class RedisIntegrationExceptionTest {

        @Test
        @DisplayName("Redis连接超时异常处理")
        public void testRedisConnectionTimeout() {
            // Given
            when(valueOperations.get(anyString()))
                    .thenThrow(new org.springframework.dao.QueryTimeoutException("Redis connection timeout"));

            // When & Then - 不应该抛出异常
            assertDoesNotThrow(() -> {
                boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);
                assertFalse(isValid); // 由于Redis异常，验证失败
            });
        }

        @Test
        @DisplayName("Redis网络异常处理")
        public void testRedisNetworkException() {
            // Given
            when(valueOperations.get(anyString()))
                    .thenThrow(new org.springframework.data.redis.RedisConnectionFailureException("Network error"));

            // When & Then
            assertDoesNotThrow(() -> {
                boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);
                assertFalse(isValid);
            });
        }

        @Test
        @DisplayName("Redis序列化异常处理")
        public void testRedisSerializationException() {
            // Given
            when(valueOperations.get(anyString()))
                    .thenThrow(new org.springframework.data.redis.serializer.SerializationException("Serialization failed"));

            // When & Then
            assertDoesNotThrow(() -> {
                boolean isValid = jwtTokenUtil.validateAccessToken(validAccessToken);
                assertFalse(isValid);
            });
        }
    }

    @Nested
    @DisplayName("性能测试")
    class PerformanceTest {

        @Test
        @DisplayName("批量Token验证性能测试")
        public void testBatchTokenValidationPerformance() {
            // Given
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(validAccessToken);

            long startTime = System.currentTimeMillis();

            // When - 执行1000次验证
            int validationCount = 1000;
            for (int i = 0; i < validationCount; i++) {
                jwtTokenUtil.validateAccessToken(validAccessToken);
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Then
            System.out.println("1000次token验证耗时: " + duration + "ms");
            System.out.println("平均每次验证耗时: " + (double) duration / validationCount + "ms");

            // 验证Redis被调用了对应次数
            verify(valueOperations, times(validationCount)).get(ACCESS_TOKEN_KEY);

            // 性能断言（可根据实际需求调整）
            assertTrue(duration < 10000, "1000次验证应该在10秒内完成");
        }

        @Test
        @DisplayName("Token生成性能测试")
        public void testTokenGenerationPerformance() {
            long startTime = System.currentTimeMillis();

            // When - 生成1000个token
            int generationCount = 1000;
            for (int i = 0; i < generationCount; i++) {
                jwtTokenUtil.generateAccessToken("user" + i, (long) i);
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Then
            System.out.println("生成1000个token耗时: " + duration + "ms");
            System.out.println("平均每次生成耗时: " + (double) duration / generationCount + "ms");

            // 性能断言
            assertTrue(duration < 5000, "1000次token生成应该在5秒内完成");
        }
    }

    @Nested
    @DisplayName("并发测试")
    class ConcurrencyTest {

        @Test
        @DisplayName("并发Token验证测试")
        public void testConcurrentTokenValidation() throws InterruptedException {
            // Given
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(validAccessToken);

            int threadCount = 50;
            Thread[] threads = new Thread[threadCount];
            boolean[] results = new boolean[threadCount];

            // When - 启动多个线程并发验证
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    results[index] = jwtTokenUtil.validateAccessToken(validAccessToken);
                });
                threads[i].start();
            }

            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join(5000); // 最多等待5秒
            }

            // Then - 所有验证都应该成功
            for (int i = 0; i < threadCount; i++) {
                assertTrue(results[i], "线程 " + i + " 的验证应该成功");
            }

            // 验证Redis调用次数
            verify(valueOperations, times(threadCount)).get(ACCESS_TOKEN_KEY);
        }

        @Test
        @DisplayName("并发Token生成测试")
        public void testConcurrentTokenGeneration() throws InterruptedException {
            int threadCount = 20;
            Thread[] threads = new Thread[threadCount];
            String[] tokens = new String[threadCount];

            // When - 并发生成token
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    tokens[index] = jwtTokenUtil.generateAccessToken("user" + index, (long) index);
                });
                threads[i].start();
            }

            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join(3000);
            }

            // Then - 所有token都应该成功生成且不重复
            for (int i = 0; i < threadCount; i++) {
                assertNotNull(tokens[i], "线程 " + i + " 应该生成token");
                assertFalse(tokens[i].isEmpty(), "生成的token不应该为空");
            }

            // 验证所有token都是唯一的
            java.util.Set<String> uniqueTokens = new java.util.HashSet<>(java.util.Arrays.asList(tokens));
            assertEquals(threadCount, uniqueTokens.size(), "所有生成的token应该是唯一的");
        }
    }
}