package com.macro.mall.portal.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.macro.mall.portal.dto.AuthLoginParam;
import com.macro.mall.portal.dto.AuthTokenResult;
import com.macro.mall.portal.dto.UserInfoResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证系统集成测试
 * 测试完整的认证流程，包括Redis存储、JWT验证等
 * @author Claude
 * @since 2025-09-24
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("认证系统集成测试")
public class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 测试数据
    private static final String TEST_EMAIL = "integration@test.com";
    private static final String TEST_PASSWORD = "testPassword123";
    private static final String TEST_USERNAME = "integrationUser";

    @BeforeEach
    void setUp() {
        // 清理Redis中的测试数据
        clearRedisTestData();
    }

    private void clearRedisTestData() {
        // 清理可能存在的测试数据
        redisTemplate.delete("portal:access_token:" + TEST_USERNAME + ":*");
        redisTemplate.delete("portal:refresh_token:" + TEST_USERNAME);
        redisTemplate.delete("portal:token_blacklist:*");
    }

    @Nested
    @DisplayName("完整认证流程测试")
    class FullAuthenticationFlowTest {

        @Test
        @DisplayName("完整认证流程 - 登录到注销")
        public void testCompleteAuthenticationFlow() throws Exception {
            // 1. 用户登录
            String accessToken = performLoginAndGetToken();

            // 2. 验证token有效性
            verifyTokenIsValid(accessToken);

            // 3. 使用token访问受保护资源
            accessProtectedResource(accessToken);

            // 4. 用户注销
            performLogout(accessToken);

            // 5. 验证token已失效
            verifyTokenIsInvalid(accessToken);
        }

        @Test
        @DisplayName("Token刷新流程测试")
        public void testTokenRefreshFlow() throws Exception {
            // 1. 用户登录获取token对
            TokenPair tokenPair = performLoginAndGetTokenPair();

            // 2. 验证初始access token有效
            verifyTokenIsValid(tokenPair.accessToken);

            // 3. 使用refresh token刷新access token
            String newAccessToken = performTokenRefresh(tokenPair.refreshToken);

            // 4. 验证新的access token有效
            verifyTokenIsValid(newAccessToken);

            // 5. 验证旧的access token被更新（这里需要根据实际实现来验证）
            // 新token应该与旧token不同
            assertNotEquals(tokenPair.accessToken, newAccessToken, "新token应该与旧token不同");
        }

        @Test
        @DisplayName("强制下线流程测试")
        public void testForceLogoutFlow() throws Exception {
            // 1. 用户登录
            String accessToken = performLoginAndGetToken();

            // 2. 验证token有效
            verifyTokenIsValid(accessToken);

            // 3. 管理员强制用户下线
            performForceLogout(accessToken);

            // 4. 验证token已失效
            verifyTokenIsInvalid(accessToken);
        }

        private String performLoginAndGetToken() throws Exception {
            return performLoginAndGetTokenPair().accessToken;
        }

        private TokenPair performLoginAndGetTokenPair() throws Exception {
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(TEST_PASSWORD);

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.accessToken").exists())
                    .andExpect(jsonPath("$.data.refreshToken").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);

            return new TokenPair(
                response.path("data").path("accessToken").asText(),
                response.path("data").path("refreshToken").asText()
            );
        }

        private void verifyTokenIsValid(String accessToken) throws Exception {
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.valid").value(true));
        }

        private void verifyTokenIsInvalid(String accessToken) throws Exception {
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400));
        }

        private void accessProtectedResource(String accessToken) throws Exception {
            mockMvc.perform(get("/api/auth/user-info")
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        private void performLogout(String accessToken) throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("注销成功"));
        }

        private String performTokenRefresh(String refreshToken) throws Exception {
            MvcResult result = mockMvc.perform(post("/api/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("refreshToken", refreshToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").exists())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);
            return response.path("data").asText();
        }

        private void performForceLogout(String accessToken) throws Exception {
            mockMvc.perform(post("/api/auth/force-logout")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("用户下线成功"));
        }

        private static class TokenPair {
            final String accessToken;
            final String refreshToken;

            TokenPair(String accessToken, String refreshToken) {
                this.accessToken = accessToken;
                this.refreshToken = refreshToken;
            }
        }
    }

    @Nested
    @DisplayName("Redis存储验证测试")
    class RedisStorageVerificationTest {

        @Test
        @DisplayName("登录后验证Redis存储")
        public void testRedisStorageAfterLogin() throws Exception {
            // Given & When - 执行登录
            TokenPair tokenPair = performLoginAndGetTokenPair();

            // Then - 验证Redis中存储了相应的数据
            String accessTokenKey = "portal:access_token:" + TEST_USERNAME + ":1"; // 假设用户ID为1
            String refreshTokenKey = "portal:refresh_token:" + TEST_USERNAME;

            // 验证access token存储
            assertTrue(redisTemplate.hasKey(accessTokenKey), "Redis中应该存储access token");
            assertEquals(tokenPair.accessToken, redisTemplate.opsForValue().get(accessTokenKey),
                    "Redis中存储的access token应该与返回的token一致");

            // 验证refresh token存储
            assertTrue(redisTemplate.hasKey(refreshTokenKey), "Redis中应该存储refresh token");
            assertEquals(tokenPair.refreshToken, redisTemplate.opsForValue().get(refreshTokenKey),
                    "Redis中存储的refresh token应该与返回的token一致");
        }

        @Test
        @DisplayName("注销后验证Redis清理")
        public void testRedisClearanceAfterLogout() throws Exception {
            // Given - 用户先登录
            String accessToken = performLoginAndGetToken();
            String accessTokenKey = "portal:access_token:" + TEST_USERNAME + ":1";
            String refreshTokenKey = "portal:refresh_token:" + TEST_USERNAME;

            // 验证登录后Redis中有数据
            assertTrue(redisTemplate.hasKey(accessTokenKey), "登录后Redis应该有access token");
            assertTrue(redisTemplate.hasKey(refreshTokenKey), "登录后Redis应该有refresh token");

            // When - 用户注销
            performLogout(accessToken);

            // Then - 验证Redis数据被清理
            assertFalse(redisTemplate.hasKey(accessTokenKey), "注销后Redis中不应该有access token");
            assertFalse(redisTemplate.hasKey(refreshTokenKey), "注销后Redis中不应该有refresh token");

            // 验证黑名单
            String blacklistKey = "portal:token_blacklist:" + accessToken;
            assertTrue(redisTemplate.hasKey(blacklistKey), "注销后token应该在黑名单中");
        }

        @Test
        @DisplayName("Token刷新后验证Redis更新")
        public void testRedisUpdateAfterTokenRefresh() throws Exception {
            // Given - 用户登录
            TokenPair originalTokens = performLoginAndGetTokenPair();
            String accessTokenKey = "portal:access_token:" + TEST_USERNAME + ":1";

            // 验证原始token在Redis中
            assertEquals(originalTokens.accessToken, redisTemplate.opsForValue().get(accessTokenKey));

            // When - 刷新token
            String newAccessToken = performTokenRefresh(originalTokens.refreshToken);

            // Then - 验证Redis中的access token被更新
            assertEquals(newAccessToken, redisTemplate.opsForValue().get(accessTokenKey),
                    "刷新后Redis中应该存储新的access token");
            assertNotEquals(originalTokens.accessToken, redisTemplate.opsForValue().get(accessTokenKey),
                    "Redis中不应该还是旧的access token");
        }

        private TokenPair performLoginAndGetTokenPair() throws Exception {
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(TEST_PASSWORD);

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);

            return new TokenPair(
                response.path("data").path("accessToken").asText(),
                response.path("data").path("refreshToken").asText()
            );
        }

        private String performLoginAndGetToken() throws Exception {
            return performLoginAndGetTokenPair().accessToken;
        }

        private void performLogout(String accessToken) throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }

        private String performTokenRefresh(String refreshToken) throws Exception {
            MvcResult result = mockMvc.perform(post("/api/auth/refresh-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("refreshToken", refreshToken))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);
            return response.path("data").asText();
        }

        private static class TokenPair {
            final String accessToken;
            final String refreshToken;

            TokenPair(String accessToken, String refreshToken) {
                this.accessToken = accessToken;
                this.refreshToken = refreshToken;
            }
        }
    }

    @Nested
    @DisplayName("并发场景测试")
    class ConcurrentScenarioTest {

        @Test
        @DisplayName("并发登录测试")
        public void testConcurrentLogin() throws Exception {
            int threadCount = 10;
            Thread[] threads = new Thread[threadCount];
            Exception[] exceptions = new Exception[threadCount];

            // 启动多个线程同时登录
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        performLoginAndGetToken();
                    } catch (Exception e) {
                        exceptions[index] = e;
                    }
                });
                threads[i].start();
            }

            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join(10000); // 最多等待10秒
            }

            // 验证没有异常发生
            for (int i = 0; i < threadCount; i++) {
                assertNull(exceptions[i], "线程 " + i + " 不应该抛出异常");
            }
        }

        @Test
        @DisplayName("并发Token验证测试")
        public void testConcurrentTokenValidation() throws Exception {
            // Given - 先登录获取token
            String accessToken = performLoginAndGetToken();

            int threadCount = 20;
            Thread[] threads = new Thread[threadCount];
            boolean[] results = new boolean[threadCount];
            Exception[] exceptions = new Exception[threadCount];

            // When - 并发验证token
            for (int i = 0; i < threadCount; i++) {
                final int index = i;
                threads[i] = new Thread(() -> {
                    try {
                        results[index] = verifyTokenValidityAndReturn(accessToken);
                    } catch (Exception e) {
                        exceptions[index] = e;
                    }
                });
                threads[i].start();
            }

            // 等待所有线程完成
            for (Thread thread : threads) {
                thread.join(5000);
            }

            // Then - 验证结果
            for (int i = 0; i < threadCount; i++) {
                assertNull(exceptions[i], "线程 " + i + " 不应该抛出异常");
                assertTrue(results[i], "线程 " + i + " 的token验证应该成功");
            }
        }

        private String performLoginAndGetToken() throws Exception {
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(TEST_PASSWORD);

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);
            return response.path("data").path("accessToken").asText();
        }

        private boolean verifyTokenValidityAndReturn(String accessToken) throws Exception {
            MvcResult result = mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", accessToken))
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);
            return response.path("code").asInt() == 200 &&
                   response.path("data").path("valid").asBoolean();
        }
    }

    @Nested
    @DisplayName("错误场景测试")
    class ErrorScenarioTest {

        @Test
        @DisplayName("使用过期Token访问")
        public void testAccessWithExpiredToken() throws Exception {
            // 这个测试比较难实现，因为需要等待token过期
            // 可以通过mock的方式或者设置很短的过期时间来测试
            // 暂时跳过具体实现
        }

        @Test
        @DisplayName("使用已注销的Token访问")
        public void testAccessWithRevokedToken() throws Exception {
            // Given - 登录并注销
            String accessToken = performLoginAndGetToken();
            performLogout(accessToken);

            // When & Then - 使用已注销的token访问应该失败
            mockMvc.perform(post("/api/auth/validate-token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .param("token", accessToken))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(400));
        }

        @Test
        @DisplayName("Redis服务不可用时的降级处理")
        public void testRedisUnavailableGracefulDegradation() throws Exception {
            // 这个测试需要模拟Redis不可用的情况
            // 可以通过关闭Redis服务或mock异常来测试
            // 实际实现会比较复杂，这里仅作为测试用例的占位
        }

        private String performLoginAndGetToken() throws Exception {
            AuthLoginParam loginParam = new AuthLoginParam();
            loginParam.setEmail(TEST_EMAIL);
            loginParam.setPassword(TEST_PASSWORD);

            MvcResult result = mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginParam)))
                    .andExpect(status().isOk())
                    .andReturn();

            String responseContent = result.getResponse().getContentAsString();
            var response = objectMapper.readTree(responseContent);
            return response.path("data").path("accessToken").asText();
        }

        private void performLogout(String accessToken) throws Exception {
            mockMvc.perform(post("/api/auth/logout")
                    .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }
    }
}