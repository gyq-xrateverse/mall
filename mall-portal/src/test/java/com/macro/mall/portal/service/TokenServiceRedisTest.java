package com.macro.mall.portal.service;

import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.service.impl.TokenServiceImpl;
import com.macro.mall.security.util.PortalJwtTokenUtil;
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

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TokenService Redis功能测试
 * 专注测试Redis存储和token管理功能
 * @author Claude
 * @since 2025-09-24
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService Redis功能测试")
public class TokenServiceRedisTest {

    @Mock
    private PortalJwtTokenUtil jwtTokenUtil;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private UmsMemberMapper memberMapper;

    @InjectMocks
    private TokenServiceImpl tokenService;

    // 测试数据
    private static final String TEST_USERNAME = "testuser";
    private static final Long TEST_USER_ID = 1L;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_ACCESS_TOKEN = "test-access-token";
    private static final String TEST_REFRESH_TOKEN = "test-refresh-token";
    private static final String NEW_ACCESS_TOKEN = "new-access-token";

    // Redis key格式
    private static final String ACCESS_TOKEN_KEY = "portal:access_token:" + TEST_USERNAME + ":" + TEST_USER_ID;
    private static final String REFRESH_TOKEN_KEY = "portal:refresh_token:" + TEST_USERNAME;
    private static final String BLACKLIST_KEY = "portal:token_blacklist:" + TEST_ACCESS_TOKEN;

    @BeforeEach
    void setUp() {
        // Mock Redis操作
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(redisTemplate.delete(anyString())).thenReturn(true);

        // 重置所有mock
        reset(jwtTokenUtil, redisTemplate, valueOperations, memberMapper);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("Access Token Redis存储测试")
    class AccessTokenRedisTest {

        @Test
        @DisplayName("生成Access Token - 成功存储到Redis")
        public void testGenerateAccessTokenStoresInRedis() {
            // Given
            when(jwtTokenUtil.generateAccessToken(TEST_USERNAME, TEST_USER_ID))
                    .thenReturn(TEST_ACCESS_TOKEN);

            // When
            String result = tokenService.generateAccessToken(TEST_USERNAME, TEST_USER_ID);

            // Then
            assertEquals(TEST_ACCESS_TOKEN, result);

            // 验证Redis存储操作
            verify(valueOperations).set(ACCESS_TOKEN_KEY, TEST_ACCESS_TOKEN, 24, TimeUnit.HOURS);
            verify(jwtTokenUtil).generateAccessToken(TEST_USERNAME, TEST_USER_ID);
        }

        @Test
        @DisplayName("Token验证 - 检查Redis存在性")
        public void testValidateTokenChecksRedis() {
            // Given - 模拟token在Redis中存在
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(TEST_ACCESS_TOKEN);
            when(jwtTokenUtil.validateAccessToken(TEST_ACCESS_TOKEN)).thenReturn(true);

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertTrue(isValid);

            // 验证Redis检查和JWT验证都被调用
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
            verify(jwtTokenUtil).validateAccessToken(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("Token验证失败 - Redis中不存在token")
        public void testValidateTokenFailsWhenNotInRedis() {
            // Given - token不在Redis中
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(null);

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isValid);

            // 验证Redis被检查但JWT验证未被调用
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
            verify(jwtTokenUtil, never()).validateAccessToken(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("Token验证失败 - Redis中token不匹配")
        public void testValidateTokenFailsWhenTokenMismatch() {
            // Given - Redis中存储的token与请求token不匹配
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn("different-token");

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isValid);

            // 验证Redis被检查但JWT验证未被调用
            verify(valueOperations).get(ACCESS_TOKEN_KEY);
            verify(jwtTokenUtil, never()).validateAccessToken(TEST_ACCESS_TOKEN);
        }
    }

    @Nested
    @DisplayName("Refresh Token Redis存储测试")
    class RefreshTokenRedisTest {

        @Test
        @DisplayName("生成Token对 - 同时存储Access和Refresh Token")
        public void testGenerateTokenPairStoresBothTokens() {
            // Given
            when(jwtTokenUtil.generateAccessToken(TEST_USERNAME, TEST_USER_ID))
                    .thenReturn(TEST_ACCESS_TOKEN);
            when(jwtTokenUtil.generateRefreshToken(TEST_USERNAME, TEST_USER_ID))
                    .thenReturn(TEST_REFRESH_TOKEN);

            // When
            Map<String, String> tokenPair = tokenService.generateTokenPair(TEST_USERNAME, TEST_USER_ID);

            // Then
            assertEquals(TEST_ACCESS_TOKEN, tokenPair.get("access_token"));
            assertEquals(TEST_REFRESH_TOKEN, tokenPair.get("refresh_token"));
            assertEquals("Bearer", tokenPair.get("token_type"));
            assertEquals("86400", tokenPair.get("expires_in"));

            // 验证Redis存储操作
            verify(valueOperations).set(ACCESS_TOKEN_KEY, TEST_ACCESS_TOKEN, 24, TimeUnit.HOURS);
            verify(valueOperations).set(REFRESH_TOKEN_KEY, TEST_REFRESH_TOKEN, 7, TimeUnit.DAYS);
        }

        @Test
        @DisplayName("刷新Access Token - 验证Redis检查和更新")
        public void testRefreshAccessTokenUpdatesRedis() {
            // Given
            when(jwtTokenUtil.validateRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
            when(jwtTokenUtil.getUsernameFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(REFRESH_TOKEN_KEY)).thenReturn(TEST_REFRESH_TOKEN);
            when(jwtTokenUtil.generateAccessToken(TEST_USERNAME, TEST_USER_ID))
                    .thenReturn(NEW_ACCESS_TOKEN);

            // When
            String newToken = tokenService.refreshAccessToken(TEST_REFRESH_TOKEN);

            // Then
            assertEquals(NEW_ACCESS_TOKEN, newToken);

            // 验证刷新token检查
            verify(valueOperations).get(REFRESH_TOKEN_KEY);
            // 验证新access token存储
            verify(valueOperations).set(ACCESS_TOKEN_KEY, NEW_ACCESS_TOKEN, 24, TimeUnit.HOURS);
        }

        @Test
        @DisplayName("刷新Token失败 - Refresh Token不在Redis中")
        public void testRefreshTokenFailsWhenNotInRedis() {
            // Given
            when(jwtTokenUtil.validateRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
            when(jwtTokenUtil.getUsernameFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(REFRESH_TOKEN_KEY)).thenReturn(null);

            // When
            String newToken = tokenService.refreshAccessToken(TEST_REFRESH_TOKEN);

            // Then
            assertNull(newToken);

            // 验证不会生成新token
            verify(jwtTokenUtil, never()).generateAccessToken(anyString(), anyLong());
        }

        @Test
        @DisplayName("刷新Token失败 - Refresh Token不匹配")
        public void testRefreshTokenFailsWhenTokenMismatch() {
            // Given
            when(jwtTokenUtil.validateRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
            when(jwtTokenUtil.getUsernameFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(REFRESH_TOKEN_KEY)).thenReturn("different-refresh-token");

            // When
            String newToken = tokenService.refreshAccessToken(TEST_REFRESH_TOKEN);

            // Then
            assertNull(newToken);

            // 验证不会生成新token
            verify(jwtTokenUtil, never()).generateAccessToken(anyString(), anyLong());
        }
    }

    @Nested
    @DisplayName("Token注销和黑名单测试")
    class TokenRevocationTest {

        @Test
        @DisplayName("注销Token - 清除Redis并添加黑名单")
        public void testRevokeTokenClearsRedisAndAddsBlacklist() {
            // Given
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);

            // When
            tokenService.revokeToken(TEST_ACCESS_TOKEN);

            // Then
            // 验证添加到黑名单
            verify(valueOperations).set(BLACKLIST_KEY, "revoked", 24, TimeUnit.HOURS);
            // 验证删除access token
            verify(redisTemplate).delete(ACCESS_TOKEN_KEY);
            // 验证删除refresh token
            verify(redisTemplate).delete(REFRESH_TOKEN_KEY);
        }

        @Test
        @DisplayName("检查Token是否被注销 - 黑名单检查")
        public void testIsTokenRevokedChecksBlacklist() {
            // Given
            when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(true);

            // When
            boolean isRevoked = tokenService.isTokenRevoked(TEST_ACCESS_TOKEN);

            // Then
            assertTrue(isRevoked);
            verify(redisTemplate).hasKey(BLACKLIST_KEY);
        }

        @Test
        @DisplayName("Token未被注销")
        public void testIsTokenNotRevoked() {
            // Given
            when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(false);

            // When
            boolean isRevoked = tokenService.isTokenRevoked(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isRevoked);
            verify(redisTemplate).hasKey(BLACKLIST_KEY);
        }

        @Test
        @DisplayName("Token验证 - 黑名单检查优先于其他验证")
        public void testValidateTokenChecksBlacklistFirst() {
            // Given - token在黑名单中
            when(redisTemplate.hasKey(BLACKLIST_KEY)).thenReturn(true);

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isValid);

            // 验证只检查了黑名单，未进行其他验证
            verify(redisTemplate).hasKey(BLACKLIST_KEY);
            verify(jwtTokenUtil, never()).getUsernameFromToken(anyString());
            verify(jwtTokenUtil, never()).validateAccessToken(anyString());
        }
    }

    @Nested
    @DisplayName("Redis异常处理测试")
    class RedisExceptionHandlingTest {

        @Test
        @DisplayName("Redis连接异常 - Token验证降级处理")
        public void testValidateTokenWithRedisException() {
            // Given - Redis操作抛出异常
            when(redisTemplate.hasKey(anyString())).thenThrow(new RuntimeException("Redis connection failed"));

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then - 应该返回false，不会导致系统崩溃
            assertFalse(isValid);
        }

        @Test
        @DisplayName("Redis写入异常 - Token生成仍然成功")
        public void testGenerateTokenWithRedisWriteException() {
            // Given
            when(jwtTokenUtil.generateAccessToken(TEST_USERNAME, TEST_USER_ID))
                    .thenReturn(TEST_ACCESS_TOKEN);
            doThrow(new RuntimeException("Redis write failed"))
                    .when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));

            // When & Then - 不应该抛出异常
            assertDoesNotThrow(() -> {
                String token = tokenService.generateAccessToken(TEST_USERNAME, TEST_USER_ID);
                assertEquals(TEST_ACCESS_TOKEN, token);
            });

            // 验证JWT token仍然被生成
            verify(jwtTokenUtil).generateAccessToken(TEST_USERNAME, TEST_USER_ID);
        }

        @Test
        @DisplayName("Redis读取异常 - 刷新Token失败")
        public void testRefreshTokenWithRedisReadException() {
            // Given
            when(jwtTokenUtil.validateRefreshToken(TEST_REFRESH_TOKEN)).thenReturn(true);
            when(jwtTokenUtil.getUsernameFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_REFRESH_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(anyString())).thenThrow(new RuntimeException("Redis read failed"));

            // When
            String newToken = tokenService.refreshAccessToken(TEST_REFRESH_TOKEN);

            // Then - 应该返回null表示失败
            assertNull(newToken);
        }
    }

    @Nested
    @DisplayName("性能和并发测试")
    class PerformanceConcurrencyTest {

        @Test
        @DisplayName("并发Token验证测试")
        public void testConcurrentTokenValidation() throws InterruptedException {
            // Given
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(TEST_ACCESS_TOKEN);
            when(jwtTokenUtil.validateAccessToken(TEST_ACCESS_TOKEN)).thenReturn(true);

            // When - 并发执行token验证
            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];
            boolean[] results = new boolean[threadCount];

            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    results[index] = tokenService.validateToken(TEST_ACCESS_TOKEN);
                });
                threads[i].start();
            }

            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join();
            }

            // Then - 所有验证都应该成功
            for (boolean result : results) {
                assertTrue(result);
            }

            // 验证Redis和JWT工具都被正确调用
            verify(valueOperations, times(threadCount)).get(ACCESS_TOKEN_KEY);
            verify(jwtTokenUtil, times(threadCount)).validateAccessToken(TEST_ACCESS_TOKEN);
        }

        @Test
        @DisplayName("Token生成性能测试")
        public void testTokenGenerationPerformance() {
            // Given
            when(jwtTokenUtil.generateAccessToken(TEST_USERNAME, TEST_USER_ID))
                    .thenReturn(TEST_ACCESS_TOKEN);

            long startTime = System.currentTimeMillis();

            // When - 生成100个token
            for (int i = 0; i < 100; i++) {
                tokenService.generateAccessToken(TEST_USERNAME, TEST_USER_ID);
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Then
            System.out.println("生成100个token耗时: " + duration + "ms");

            // 验证所有操作都被执行
            verify(jwtTokenUtil, times(100)).generateAccessToken(TEST_USERNAME, TEST_USER_ID);
            verify(valueOperations, times(100)).set(eq(ACCESS_TOKEN_KEY), eq(TEST_ACCESS_TOKEN), eq(24L), eq(TimeUnit.HOURS));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTest {

        @Test
        @DisplayName("空用户名和用户ID处理")
        public void testNullUsernameAndUserId() {
            // Given
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(null);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(null);

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isValid);

            // 验证不会尝试Redis操作
            verify(valueOperations, never()).get(anyString());
        }

        @Test
        @DisplayName("部分用户信息为空")
        public void testPartialNullUserInfo() {
            // Given - 只有用户名为空
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(null);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isValid);

            // 验证不会尝试Redis操作
            verify(valueOperations, never()).get(anyString());
        }

        @Test
        @DisplayName("Redis返回空值处理")
        public void testRedisReturnsNull() {
            // Given
            when(jwtTokenUtil.getUsernameFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USERNAME);
            when(jwtTokenUtil.getUserIdFromToken(TEST_ACCESS_TOKEN)).thenReturn(TEST_USER_ID);
            when(valueOperations.get(ACCESS_TOKEN_KEY)).thenReturn(null);

            // When
            boolean isValid = tokenService.validateToken(TEST_ACCESS_TOKEN);

            // Then
            assertFalse(isValid);

            // 验证不会进行JWT验证
            verify(jwtTokenUtil, never()).validateAccessToken(anyString());
        }
    }
}